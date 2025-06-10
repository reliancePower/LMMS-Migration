(function() {
	'use strict';

	angular.module('regCalApp.manageforum').controller('ManageForumController',
			ManageForumController);

	ManageForumController.$inject = [ '$state', 'ManageForumService', '$scope',
			'localStorageService', 'SearchCaseService', '$filter', '$window',
			'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder',
			'$uibModal', '$uibModalStack','navbarService' ];

	/* @ngInject */
	function ManageForumController($state, ManageForumService, $scope,
			localStorageService, SearchCaseService, $filter, $window,
			DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $uibModal,
			$uibModalStack, navbarService) {
		var vm = angular.extend(this, {

			dtOptions : {},
			form : {
				forumCategory : '',
				state : '',
				caseType : '',
				forumName : ''
			},
			forumCategoryArray : [],
			stateInfo : [],
			submit : submit,
			fetchForums : fetchForums,
			forumCategory : '',
			forumArray : [],
			forumArrayAvailable : [],
			resetFilter : resetFilter,
			addForum : addForum,
			selItem : {}

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.userID = localStorageService.get('payrollNo');
			vm.userType = localStorageService.get('userType');
			vm.caseTypeInfo = [ 'Others' ];
			vm.form.caseType = 'Others';

			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'autoWidth', false)

			var jsonReq = {
				"type" : "fetchMaster",
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq).then(
					function(result) {
						if(result.data.status == 'S'){
							alert(result.data.msg);
							if (localStorageService.get('payrollNo') == undefined
									|| localStorageService.get('payrollNo') == '') {
								$window.localStorage.clear();
								localStorageService.clearAll;
								localStorageService.cookie.clearAll;
								$state.go('login');
							} else {
								navbarService
										.logout()
										.then(
												function(result) {
													$window.localStorage.clear();
													localStorageService.clearAll;
													localStorageService.cookie.clearAll;
													$state.go('login');
												},
												function caseError(error) {
													localStorageService.clearAll;
													localStorageService.cookie.clearAll;
													$state.go('login');
													console
															.log('There seems some problem. Please try again later...');

												});
							}
						}
//						if(result.data.status == 'F'){
//							alert(result.data.msg);
//							return false;
//						}
						vm.forumCategoryArray = result.data.forumCategoryArray;
						vm.forumCategoryArray1 = _.without(
								vm.forumCategoryArray, 'supreme court');
						vm.forumCategoryArray1 = _.without(
								vm.forumCategoryArray1, 'highcourt');
						console.log(vm.forumCategoryArray1);
						vm.stateInfo = result.data.stateArray;

					}, function loginError(error) {

						alert('Something went wrong... Pls try again...');
						return false;
					});

		})();

		function addForum() {
			var jsonReq = {
				"type" : "sendForumReq",
				"category" : vm.form.forumCategory,
				"state" : vm.form.state,
				"name" : vm.form.forumName,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService
					.manageForum(jsonReq)
					.then(
							function(result) {
								console.log(result.data)
								if(result.data.status == 'F'){
									alert(result.data.msg);
									return false;
								}
								if (result.data.status == 'T') {
									$uibModalStack.dismissAll();
									alert("Your request for adding a forum is sent successfully...");
									vm.form.forumCategory = '';
									vm.form.state = '';
									vm.form.caseType = '';
									vm.form.forumName = '';
								}

							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								$uibModalStack.dismissAll();
								return false;
							});

		}

		function fetchForums() {
			var jsonReq = {
				"type" : "fetchForum",
				"category" : vm.forumCategory,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				console.log(result.data);
				vm.forumArray = result.data.resultArray;
				$('#tableContainer').css('display', 'block');

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		function submit(form) {
			angular.forEach(form, function(obj) {
				if (angular.isObject(obj) && angular.isDefined(obj.$setDirty)) {
					obj.$setDirty();
				}
			})

			if (form.$valid) {
				var jsonReq = {
					"type" : "checkDB",
					"category" : form.forumCategory.$modelValue,
					"state" : form.state.$modelValue,
					"name" : form.forumName.$modelValue,
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				ManageForumService
						.manageForum(jsonReq)
						.then(
								function(result) {
									if(result.data.status == 'F'){
										alert(result.data.msg);
										return false;
									}
									console.log(result.data)
									vm.forumArrayAvailable = result.data.forumArray;
									if (vm.forumArrayAvailable != undefined) {
										var modalInstance = $uibModal.open({
											ariaLabelledBy : 'modal-title',
											ariaDescribedBy : 'modal-body',
											templateUrl : 'viewForum.html',
											size : 'md',
											backdrop : 'static',
											keyboard : false,
											windowClass : 'userFillWindow',
											scope : $scope
										});

										modalInstance.result.then(function(
												response) {
										}, function() {
											console.log('Modal dismissed at: '
													+ new Date());
										});
									} else {
										if (result.data.status == 'T') {
											alert("Your request for adding a forum is sent successfully...");
											vm.form.forumCategory = '';
											vm.form.state = '';
											vm.form.caseType = '';
											vm.form.forumName = '';
										}
									}

								},
								function loginError(error) {

									alert('Something went wrong... Pls try again...');
									return false;
								});
			} else {
				return false;
			}

		}

		function resetFilter() {

			vm.form.forumCategory = '';
			vm.form.state = '';
			vm.form.caseType = '';
			vm.form.forumName = '';
			$('#addForumFormContainer').collapse('hide');

		}

	}

})();
