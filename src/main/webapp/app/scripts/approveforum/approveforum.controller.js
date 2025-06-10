(function() {
	'use strict';

	angular.module('regCalApp.approveforum').controller(
			'ApproveForumController', ApproveForumController);

	ApproveForumController.$inject = [ '$state', 'ApproveForumService',
			'DTOptionsBuilder', 'DTColumnBuilder', '$scope',
			'localStorageService', '$compile', '$window', 'ManageForumService',
			'$uibModal', '$uibModalStack' ];

	/* @ngInject */
	function ApproveForumController($state, ApproveForumService,
			DTOptionsBuilder, DTColumnBuilder, $scope, localStorageService,
			$compile, $window, ManageForumService, $uibModal, $uibModalStack) {
		var vm = angular.extend(this, {
			dtOptions : {},
			today : new Date(),
			form : {
				forumCategory : '',
				state : '',
				caseType : '',
				forumName : ''
			},
			editform : {
				forumCategory : '',
				forumName : '',
				remark : ''
			},
			approveRequestArray : [],
			dtColumnDefs : [],
			fetchForums : fetchForums,
			editForumItem : editForumItem,
			deleteForum : deleteForum,
			approveForumReq : approveForumReq,
			rejectForumReq : rejectForumReq,
			rejectForumItem : rejectForumItem,
			approveForumItem : approveForumItem,
			editForum : editForum

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.userID = localStorageService.get('payrollNo');
			vm.userType = localStorageService.get('userType');
			getPageInfo();
			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'responsive', true);

		})();

		function getPageInfo() {

			var jsonReq = {
				"type" : "fetchForumReq",
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.approveRequestArray = result.data.resultArray;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
			var jsonReq1 = {
				"type" : "fetchMaster",
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq1).then(
					function(result) {
						if(result.data.status == 'F'){
							alert(result.data.msg);
							return false;
						}
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

		}

		function approveForumItem(val) {
			vm.isEdit = true;
			var item = val;
			vm.selItem = item;

			vm.editform.forumCategory = item.category;
			vm.editform.forumName = item.name;
			vm.editform.remark = '';

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'approveForum.html',
				size : 'md',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
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
		function editForumItem(val) {

			vm.isEdit = true;
			var item = val;
			vm.selItem = item;

			vm.editform.forumCategory = item.category;
			vm.editform.forumName = item.name;

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'editForum.html',
				size : 'md',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

		}

		function rejectForumItem(val) {
			vm.isEdit = true;
			var item = val;
			vm.selItem = item;

			vm.editform.forumCategory = item.category;
			vm.editform.forumName = item.name;
			vm.editform.remark = '';

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'rejectForum.html',
				size : 'md',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}
		function editForum() {

			if (confirm('Are you sure to edit the forum with name '
					+ vm.selItem.name)) {
				var jsonReq = {
					"type" : "editForum",
					"category" : vm.editform.forumCategory,
					"name" : vm.editform.forumName,
					"id" : vm.selItem.id,
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

									if (result.data.status == 'T') {
										$uibModalStack.dismissAll();
										alert("Forum details edited successfully with ID "
												+ result.data.forumID);
										fetchForums();
									}

								},
								function loginError(error) {

									alert('Something went wrong... Pls try again...');
									$uibModalStack.dismissAll();
									return false;
								});
			} else {
				return false
			}
		}
		function deleteForum(item) {
			vm.selItem = item;
			if (confirm('Are you sure to delete the forum with name '
					+ vm.selItem.name)) {
				var jsonReq = {
					"type" : "deleteForum",
					"category" : vm.selItem.category,
					"id" : vm.selItem.id,
					"name" : vm.selItem.name,
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				ManageForumService.manageForum(jsonReq).then(
						function(result) {
							if(result.data.status == 'F'){
								alert(result.data.msg);
								return false;
							}
							console.log(result.data)

							if (result.data.status == 'T') {
								$uibModalStack.dismissAll();
								alert("Forum deleted successfully with ID "
										+ result.data.forumID);
								fetchForums();
							}

						}, function loginError(error) {

							alert('Something went wrong... Pls try again...');
							$uibModalStack.dismissAll();
							return false;
						});
			} else {
				return false
			}
		}

		function approveForumReq() {

			if (confirm('Are you sure to approve the forum with name '
					+ vm.editform.forumName)) {
				var jsonReq = {
					"type" : "approveForumReq",
					"category" : vm.editform.forumCategory,
					"name" : vm.editform.forumName,
					"remark" : vm.editform.remark,
					"id" : vm.selItem.id,
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				ManageForumService.manageForum(jsonReq).then(
						function(result) {
							if(result.data.status == 'F'){
								alert(result.data.msg);
								return false;
							}
							console.log(result.data)

							if (result.data.status == 'T') {
								$uibModalStack.dismissAll();
								alert("Forum approved successfully with name "
										+ vm.editform.forumName);
								getPageInfo();
							}

						}, function loginError(error) {

							alert('Something went wrong... Pls try again...');
							$uibModalStack.dismissAll();
							return false;
						});
			} else {
				return false
			}

		}

		function rejectForumReq() {
			if (confirm('Are you sure to reject this forum')) {
				var jsonReq = {
					"type" : "rejectForumReq",
					"category" : vm.editform.forumCategory,
					"name" : vm.editform.forumName,
					"remark" : vm.editform.remark,
					"id" : vm.selItem.id,
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				ManageForumService
						.manageForum(jsonReq)
						.then(
								function(result) {
									if (result.data.status == 'T') {
										alert('Forum request rejected successfully');
										$state.reload();
										//$window.location.reload();
									} else {
										alert('Not able to reject forum request... Please try again later');
										return false;
									}

								},
								function caseError(error) {
									alert('There seems some problem. Please try again later...');
									return false;
								});

			} else {
				console.log('false');
				return false;
			}

		}

	}
})();
