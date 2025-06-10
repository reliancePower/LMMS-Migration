(function() {
	'use strict';

	angular.module('regCalApp.adduser').controller('AddUserController',
			AddUserController);

	AddUserController.$inject = [ '$state', 'AddUserService', '$scope',
			'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
			'$uibModal', '$uibModalStack', 'dashboardService', '$sce',
			'$rootScope', '$window', 'addCaseService', 'navbarService' ];

	/* @ngInject */
	function AddUserController($state, AddUserService, $scope,
			localStorageService, DTOptionsBuilder, DTColumnBuilder, $uibModal,
			$uibModalStack, dashboardService, $sce, $rootScope, $window,
			addCaseService, navbarService) {
		var vm = angular.extend(this, {
			form : {
				payrollNo : '',
				userName : '',
				webMail : '',
				mobileNo : '',
				emailID : '@relianceada.com',
				userType : '',
				userPlant : '',
				accessType : '',
				dob : '01012018',
				company : '',
				business : '',
				vertical : '',
				caseType : '',
				imeiNo1 : '',
				imeiNo2 : '',
				imeiNo3 : ''
			},
			submit : submit,
			getEmail : getEmail
		// userTypeInfo : [{'id' : 1 , 'name' : 'Admin'}, {'id' : 2 , 'name' :
		// 'Employee'}, {'id' : 2 , 'name' : 'Key User'}]

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			fetchAllMaster();
			localStorageService.remove('searchCaseDetails');
		})();

		// $scope.$watch('vm.businessInfo', function(newValue, oldValue, scope)
		// {
		// console.log("O: ", newValue, oldValue, scope);
		// // Attributes
		// if (newValue.length > 0) {
		// $scope.$evalAsync(function() { $('#business').multiselect('refresh');
		// });
		// // Or : $('#myMutiSelect1').multiSelect('refresh');
		// }
		// });

		function fetchAllMaster() {
			var jsonReq = {
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID'),
				"type" : "fetchMasterForUserAdd"
			}
			addCaseService
					.fetchAllMasters(jsonReq)
					.then(
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
//								if(result.data.status == 'F'){
//									alert(result.data.msg);
//									return false;
//								}
								// AddUserService.fetchAllMasterUser()
								// .then(function(result) {
								vm.accessTypeInfo = result.data.userAccessMaster;
								vm.userTypeInfo = result.data.userTypeMaster;
								vm.companyInfo = result.data.companyMaster;
								vm.businessInfo = result.data.businessArray;
								vm.verticalInfo = result.data.verticalArray;
								vm.caseTypeInfo = result.data.caseTypeMaster;

								$(document)
										.ready(
												function() {

													$('#company')
															.multiselect(
																	{
																		templates: {
														 			          
													 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
													 			        },
													 			        buttonContainer: '<div class="dropdown" />',
													 			        buttonClass: 'btn btn-white',
													 			        enableFiltering: true,
													 			        enableCaseInsensitiveFiltering: true, 
																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 4,
																		nonSelectedText : 'Select Company',
																		selectAllValue : 0,
																		selectAllName : 'company-select',
																		onChange : function(
																				element,
																				checked) {

																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																			var selComp = $(
																					'#company')
																					.val();
																			if (selComp.length == 0
																					|| selComp.length == vm.companyInfo.length)
																				return false;
																			vm.vertiInfo = [];
																			$(
																					"#vertical")
																					.empty();
																			$(
																					"#vertical")
																					.multiselect(
																							"rebuild");

																			vm.busiInfo = [];
																			// $("#business").val([]);
																			$(
																					'#business')
																					.empty();
																			$(
																					"#business")
																					.multiselect(
																							"refresh");

																			var jsonReq = {
																				"type" : "business",
																				"id" : selComp
																						.toString(),
																						"userID" : localStorageService.get('payrollNo'),
																						"sessID" : localStorageService.get('sessionID')
																			}
																			AddUserService
																					.fetchBusiVeriView(
																							jsonReq)
																					.then(
																							function(
																									result) {
																								if(result.data.status == 'F'){
																									alert(result.data.msg);
																									return false;
																								}
																								vm.busiInfo = result.data.resultArray;
																								for (var i = 0; i < vm.busiInfo.length; i++) {
																									$(
																											'#business')
																											.append(
																													'<option value="'
																															+ vm.busiInfo[i].id
																															+ '">'
																															+ vm.busiInfo[i].name
																															+ '</option>');
																								}
																								$(
																										"#business")
																										.multiselect(
																												"rebuild");

																							},
																							function caseError(
																									error) {
																								console
																										.log('There seems some problem. Please try again later...');
																								return false;
																							});

																		},
																		onSelectAll : function() {
																			vm.vertiInfo = [];
																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																			var selComp = $(
																					'#company')
																					.val();
																			if (selComp.length == 0) {
																				vm.vertiInfo = [];
																				$(
																						'#vertical')
																						.empty();
																				$(
																						"#vertical")
																						.multiselect(
																								"rebuild");
																				vm.busiInfo = [];
																				$(
																						'#business')
																						.empty();
																				$(
																						"#business")
																						.multiselect(
																								"refresh");
																				return false;

																			}
																			vm.vertiInfo = [];
																			$(
																					'#vertical')
																					.empty();
																			$(
																					"#vertical")
																					.multiselect(
																							"rebuild");
																			vm.busiInfo = [];
																			$(
																					'#business')
																					.empty();
																			$(
																					"#business")
																					.multiselect(
																							"refresh");

																			var jsonReq = {
																				"type" : "business",
																				"id" : selComp
																						.toString(),
																						"userID" : localStorageService.get('payrollNo'),
																						"sessID" : localStorageService.get('sessionID')
																			}
																			AddUserService
																					.fetchBusiVeriView(
																							jsonReq)
																					.then(
																							function(
																									result) {
																								if(result.data.status == 'F'){
																									alert(result.data.msg);
																									return false;
																								}
																								vm.busiInfo = result.data.resultArray;
																								for (var i = 0; i < vm.busiInfo.length; i++) {
																									$(
																											'#business')
																											.append(
																													'<option value="'
																															+ vm.busiInfo[i].id
																															+ '">'
																															+ vm.busiInfo[i].name
																															+ '</option>');
																								}
																								$(
																										"#business")
																										.multiselect(
																												"rebuild");

																							},
																							function caseError(
																									error) {
																								console
																										.log('There seems some problem. Please try again later...');
																								return false;
																							});
																		}
																	});

													$('#business')
															.multiselect(
																	{
																		templates: {
														 			          
													 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
													 			        },
													 			        buttonContainer: '<div class="dropdown" />',
													 			        buttonClass: 'btn btn-white',
													 			        enableFiltering: true,
													 			        enableCaseInsensitiveFiltering: true, 
																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 1,
																		nonSelectedText : 'Select Business',
																		selectAllValue : 0,
																		selectAllName : 'business-select',
																		onChange : function(
																				element,
																				checked) {
																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																			var selComp = $(
																					'#business')
																					.val();
																			if (selComp.length == 0
																					|| selComp.length == vm.busiInfo.length)
																				return false;
																			vm.vertiInfo = [];
																			$(
																					'#vertical')
																					.empty();
																			$(
																					"#vertical")
																					.multiselect(
																							"refresh");
																			var jsonReq = {
																				"type" : "vertical",
																				"id" : selComp
																						.toString(),
																						"userID" : localStorageService.get('payrollNo'),
																						"sessID" : localStorageService.get('sessionID')
																			}
																			AddUserService
																					.fetchBusiVeriView(
																							jsonReq)
																					.then(
																							function(
																									result) {
																								if(result.data.status == 'F'){
																									alert(result.data.msg);
																									return false;
																								}
																								vm.vertiInfo = result.data.resultArray;
																								for (var i = 0; i < vm.vertiInfo.length; i++) {
																									$(
																											'#vertical')
																											.append(
																													'<option value="'
																															+ vm.vertiInfo[i].id
																															+ '">'
																															+ vm.vertiInfo[i].fullname
																															+ '</option>');
																								}
																								$(
																										"#vertical")
																										.multiselect(
																												"rebuild");

																							},
																							function caseError(
																									error) {
																								console
																										.log('There seems some problem. Please try again later...');
																								return false;
																							});

																		},
																		onSelectAll : function() {
																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																			var selComp = $(
																					'#business')
																					.val();
																			if (selComp.length == 0) {
																				$(
																						'#vertical')
																						.empty();
																				$(
																						"#vertical")
																						.multiselect(
																								"rebuild");
																				return false;
																			}

																			$(
																					'#vertical')
																					.empty();
																			$(
																					"#vertical")
																					.multiselect(
																							"rebuild");
																			var jsonReq = {
																				"type" : "vertical",
																				"id" : selComp
																						.toString(),
																						"userID" : localStorageService.get('payrollNo'),
																						"sessID" : localStorageService.get('sessionID')
																			}
																			AddUserService
																					.fetchBusiVeriView(
																							jsonReq)
																					.then(
																							function(
																									result) {
																								if(result.data.status == 'F'){
																									alert(result.data.msg);
																									return false;
																								}
																								vm.vertiInfo = result.data.resultArray;
																								for (var i = 0; i < vm.vertiInfo.length; i++) {
																									$(
																											'#vertical')
																											.append(
																													'<option value="'
																															+ vm.vertiInfo[i].id
																															+ '">'
																															+ vm.vertiInfo[i].fullname
																															+ '</option>');
																								}
																								$(
																										"#vertical")
																										.multiselect(
																												"rebuild");

																							},
																							function caseError(
																									error) {
																								console
																										.log('There seems some problem. Please try again later...');
																								return false;
																							});
																		}
																	});

													$('#vertical')
															.multiselect(
																	{
																		templates: {
														 			          
													 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
													 			        },
													 			        buttonContainer: '<div class="dropdown" />',
													 			        buttonClass: 'btn btn-white',
													 			        enableFiltering: true,
													 			        enableCaseInsensitiveFiltering: true, 
																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 1,
																		nonSelectedText : 'Select Vertical',
																		selectAllValue : 0,
																		selectAllName : 'vertical-select',
																		onChange : function(
																				element,
																				checked) {
																			// $('ul.dropdown-menu').removeClass('show');
																		},
																		onSelectAll : function() {
																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																		}

																	});

													$('#caseType')
															.multiselect(
																	{
																		templates: {
														 			          
													 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
													 			        },
													 			        buttonContainer: '<div class="dropdown" />',
													 			        buttonClass: 'btn btn-white',
													 			        enableFiltering: true,
													 			        enableCaseInsensitiveFiltering: true, 
																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 2,
																		nonSelectedText : 'Select CaseType',
																		selectAllValue : 0,
																		selectAllName : 'caseType-select',
																		onChange : function(
																				element,
																				checked) {
																			// $('ul.dropdown-menu').removeClass('show');
																		},
																		onSelectAll : function() {
																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																		}

																	});

												});

							},
							function caseError(error) {
								alert('There seems some problem. Please try again later...');
								return false;
							});

		}

		function getEmail() {
			vm.form.emailID = vm.form.webMail.replace(/\s+/g, '.')
					+ '@relianceada.com';
		}

		function submit(form) {
			angular.forEach(form, function(obj) {
				if (angular.isObject(obj) && angular.isDefined(obj.$setDirty)) {
					obj.$setDirty();
				}
			})

			if (form.$valid) {

				var comp = '';
				var busi = '';
				var vert = '';
				if (vm.companyInfo.length == $('#company').val().length)
					comp = '0';
				else
					comp = $('#company').val().toString();

				if (vm.businessInfo.length == $('#business').val().length)
					busi = '0';
				else
					busi = $('#business').val().toString();

				if (vm.verticalInfo.length == $('#vertical').val().length)
					vert = '0';
				else
					vert = $('#vertical').val().toString();

				var jsonReq = {
					"payrollNo" : form.payrollNo.$modelValue,
					"userName" : form.userName.$modelValue,
					"webMail" : form.webMail.$modelValue,
					"mobileNo" : form.mobileNo.$modelValue,
					"emailID" : form.emailID.$modelValue,
					"userType" : form.userType.$modelValue.name,
					"accessType" : form.accessType.$modelValue.name,
					"dob" : form.dob.$modelValue,
					"imeiNo1" : form.imeiNo1.$modelValue,
					"imeiNo2" : form.imeiNo2.$modelValue,
					"imeiNo3" : form.imeiNo3.$modelValue,
					"company" : comp,
					"business" : busi,
					"vertical" : vert,
					"caseType" : $('#caseType').val().toString(),
					"enteredBy" : localStorageService.get('payrollNo'),
					"type" : 'add',
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				//alert(JSON.stringify(jsonReq));
				AddUserService.addNewUser(jsonReq).then(function(result) {

					if (result.data.status == 'T') {
						alert('User details added successfully');
						$state.reload();
						//$window.location.reload();

					} else {
						alert(result.data.msg);
						return false;
					}
				}, function loginError(error) {

					alert('Something went wrong... Pls try again...');
					return false;
				});

			} else
				return;

		}

		//		 function getValue(array){
		//			 var result = []; 
		//			 _.each(array, function(val, key){      			
		//      			result.push(parseInt(val.substring(7))); 
		//      			return result;
		//      		});
		//		
		//		}
	}

})();
