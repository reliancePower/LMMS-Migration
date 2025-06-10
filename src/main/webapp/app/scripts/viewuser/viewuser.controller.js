(function() {
	'use strict';

	angular.module('regCalApp.viewuser').controller('ViewUserController',
			ViewUserController);

	ViewUserController.$inject = [ '$state', 'viewUserService', '$scope',
			'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
			'$uibModal', '$uibModalStack', '$compile', 'AddUserService',
			'$rootScope', '$window', 'addCaseService', 'navbarService' ];

	/* @ngInject */
	function ViewUserController($state, viewUserService, $scope,
			localStorageService, DTOptionsBuilder, DTColumnBuilder, $uibModal,
			$uibModalStack, $compile, AddUserService, $rootScope, $window,
			addCaseService, navbarService) {
		var vm = angular.extend(this, {
			editUserItem : editUserItem,
			getEmail : getEmail,
			submit : submit,
			cancel : cancel,
			dtOptions : {},
			disableUserItem : disableUserItem,
			form : {
				payrollNo : '',
				userName : '',
				webMail : '',
				mobileNo : '',
				emailID : '@relianceada.com',
				userType : '',
				userPlant : '',
				accessType : '',
				dob : '',
				company : '',
				business : '',
				vertical : '',
				caseType : '',
				imeiNo1 : '',
				imeiNo2 : '',
				imeiNo3 : ''
			},
			comp_array : '',
			business_array : '',
			vertical_array : '',
			caseType_array : '',
			confirmDeleteItem : confirmDeleteItem,
			disableUserReason : [],
			reason : '',
			remark : '',
			disabledUsersArray : []
		// userTypeInfo : [{'id' : 1 , 'name' : 'Admin'}, {'id' : 2 , 'name' :
		// 'Employee'}, {'id' : 2 , 'name' : 'Key User'}]
		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			getAllUsers();
			getInactiveAllUsers();
			fetchAllMaster();
			localStorageService.remove('searchCaseDetails');
			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'responsive', true);

		})();

		function getAllUsers() {
			var data = {
				"payrollNo" : localStorageService.get('payrollNo'),
				'type' : 'view',
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			};
			viewUserService.getAllUsers(data).then(function(result) {
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
//				if(result.data.status == 'F'){
//					alert(result.data.msg);
//					return false;
//				}
				vm.userArray = result.data.userArrayMaster;

			}, function userError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function getInactiveAllUsers() {
			var data = {
				"payrollNo" : localStorageService.get('payrollNo'),
				'type' : 'inactiveUsers',
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			};
			viewUserService.getAllUsers(data).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.disabledUsersArray = result.data.disabledUsers;

			}, function userError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function fetchAllMaster() {
			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"type" : "fetchMasterForUserEdit",
				"sessID" : localStorageService.get('sessionID')
			}
			addCaseService.fetchAllMasters(jsonReq).then(function(result) {

				vm.accessTypeInfo = result.data.userAccessMaster;
				vm.userTypeInfo = result.data.userTypeMaster;
				vm.companyInfo = result.data.companyMaster;
				vm.businessInfo = result.data.businessArray;
				vm.verticalInfo = result.data.verticalArray;
				vm.caseTypeInfo = result.data.caseTypeMaster;
				vm.disableUserReason = result.data.disableUserReason;
				// AddUserService.fetchAllMaster()
				// .then(function(result) {
				// vm.accessTypeInfo = result.data.userAccessArray;
				// vm.companyInfo = result.data.companyArray;
				// vm.businessInfo = result.data.businessArray;
				// vm.verticalInfo = result.data.verticalArray;
				// vm.caseTypeInfo = result.data.caseTypeArray;

			}, function userError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function confirmDeleteItem(item) {
			vm.delItem = item;
			vm.delUser = item.userName;
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'confirmDelete.html',
				size : 'md',
				backdrop : 'static',
				keyboard : false,
				windowClass : 'userFillWindow',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				vm.disabled = 0;
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function disableUserItem() {

			var data = {
				"payrollNo" : vm.delItem.userID,
				'type' : 'disable',
				"deletedBy" : localStorageService.get('payrollNo'),
				"reason" : vm.reason,
				"remark" : vm.remark,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			};
			viewUserService.disableUserItem(data).then(function(result) {
				if (result.data.status == 'T') {
					alert('User deleted successfully');
					$state.reload();
					// $window.location.reload();

				} else {
					alert(result.data.msg);
					return false;
				}

			}, function userError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function editUserItem(item) {

			viewUserService
					.fetchUserDetails(item.userID)
					.then(
							function(result) {
								if(result.data.status == 'F'){
									alert(result.data.msg);
									return false;
								}
								vm.accessSelArray = result.data.accessLevelArrayMaster;
								vm.selMaster = result.data.selMaster;
								vm.form.payrollNo = item.userID;
								vm.form.userName = item.userName;
								vm.form.webMail = item.webMailID;
								vm.form.mobileNo = item.mobileNo;
								vm.form.emailID = item.emailID;
								vm.form.userType = item.userType;
								vm.form.accessType = item.accessType;
								vm.form.dob = item.dob;
								vm.form.imeiNo1 = item.imeiNo1;
								vm.form.imeiNo2 = item.imeiNo2;
								vm.form.imeiNo3 = item.imeiNo3;
								$(document)
										.ready(
												function() {
													$('#accessType').val(
															item.accessType);
													$('#userType').val(
															item.userType);

													var strArr1 = vm.selMaster[0]
															.split(',');
													var intArr1 = [];
													if (vm.selMaster[0]
															.indexOf(',') > 0) {
														for (var i = 0; i < strArr1.length; i++)
															intArr1
																	.push(parseInt(strArr1[i]));
													} else
														intArr1
																.push(vm.selMaster[0]);

													var strArr2 = vm.selMaster[1]
															.split(',');
													var intArr2 = [];

													if (vm.selMaster[1]
															.indexOf(',') > 0) {
														for (var i = 0; i < strArr2.length; i++)
															intArr2
																	.push(parseInt(strArr2[i]));
													} else
														intArr2
																.push(vm.selMaster[1]);

													var strArr3 = vm.selMaster[2]
															.split(',');
													var intArr3 = [];

													if (vm.selMaster[2]
															.indexOf(',') > 0) {
														for (var i = 0; i < strArr3.length; i++)
															intArr3
																	.push(parseInt(strArr3[i]));
													} else
														intArr3
																.push(vm.selMaster[2]);

													var strArr4 = vm.selMaster[3]
															.split(',');
													var intArr4 = [];

													if (vm.selMaster[3]
															.indexOf(',') > 0) {
														for (var i = 0; i < strArr4.length; i++)
															intArr4
																	.push(parseInt(strArr4[i]));
													} else
														intArr4
																.push(vm.selMaster[3]);

													vm.comp_array = _
															.uniq(intArr1);
													vm.business_array = _
															.uniq(intArr2);
													vm.vertical_array = _
															.uniq(intArr3);
													vm.caseType_array = _
															.uniq(intArr4);

													for (var i = 0; i < vm.companyInfo.length; i++) {
														$('#company')
																.append(
																		'<option value="'
																				+ vm.companyInfo[i].id
																				+ '">'
																				+ vm.companyInfo[i].name
																				+ '</option>');
													}

													$('#company')
															.multiselect(
																	{

																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 4,
																		nonSelectedText : 'Select Company',
																		selectAllValue : 0,
																		selectAllName : 'company-select'
																	});
													var temp1 = [];
													var varA = _
															.map(
																	vm.comp_array,
																	function(
																			num,
																			key) {
																		_
																				.each(
																						_
																								.where(
																										vm.businessInfo,
																										{
																											'compID' : parseInt(num)
																										}),
																						function(
																								a,
																								b) {
																							temp1
																									.push(a);
																						})

																		return _
																				.where(
																						vm.businessInfo,
																						{
																							'compID' : parseInt(num)
																						})

																	});
													for (var i = 0; i < temp1.length; i++) {
														$('#business')
																.append(
																		'<option value="'
																				+ temp1[i].id
																				+ '">'
																				+ temp1[i].name
																				+ '</option>');
													}

													$("#business")
															.multiselect(
																	{
																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 4,
																		nonSelectedText : 'Select Business',
																		selectAllValue : 0,
																		selectAllName : 'business-select'
																	});
													var temp = []
													var varB = _
															.map(
																	vm.business_array,
																	function(
																			num,
																			key) {
																		_
																				.each(
																						_
																								.where(
																										vm.verticalInfo,
																										{
																											'busiID' : parseInt(num)
																										}),
																						function(
																								a,
																								b) {
																							temp
																									.push(a);
																						})

																		return _
																				.where(
																						vm.verticalInfo,
																						{
																							'busiID' : parseInt(num)
																						})

																	});

													for (var i = 0; i < temp.length; i++) {
														$('#vertical')
																.append(
																		'<option value="'
																				+ temp[i].id
																				+ '">'
																				+ temp[i].fullname
																				+ '</option>');
													}

													$('#company')
															.change(
																	function() {
																		$(
																				'ul.dropdown-menu')
																				.removeClass(
																						'show');
																		var selComp = $(
																				'#company')
																				.val();
																		vm.vertiInfo = [];
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
																							"rebuild");
																			return false;

																		}
																		$(
																				"#vertical")
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
																			"userID" : localStorageService
																					.get('payrollNo'),
																			"sessID" : localStorageService
																					.get('sessionID')
																		}
																		AddUserService
																				.fetchBusiVeri(
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
																											'select',
																											vm.business_array);
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
																	})

													$('#business')
															.change(
																	function() {
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
																			"userID" : localStorageService
																					.get('payrollNo'),
																			"sessID" : localStorageService
																					.get('sessionID')
																		}
																		AddUserService
																				.fetchBusiVeri(
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
																											'select',
																											vm.vertical_array);
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
																	})

													$('#vertical')
															.multiselect(
																	{

																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 1,
																		nonSelectedText : 'Select Vertical',
																		selectAllValue : 0,
																		selectAllName : 'vertical-select',

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

																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 2,
																		nonSelectedText : 'Select CaseType',
																		selectAllValue : 0,
																		selectAllName : 'caseType-select',
																		onSelectAll : function() {
																			$(
																					'ul.dropdown-menu')
																					.removeClass(
																							'show');
																		}

																	});

													if (vm.accessSelArray[0].company == 0) {
														$("#company")
																.multiselect(
																		'selectAll',
																		false);
														$("#company")
																.multiselect(
																		'updateButtonText');

													} else {

														$("#company")
																.multiselect(
																		'select',
																		vm.comp_array);

													}
													if (vm.accessSelArray[0].business == 0) {
														$("#business")
																.multiselect(
																		'selectAll',
																		false);
														$("#business")
																.multiselect(
																		'updateButtonText');

													} else {

														$("#business")
																.multiselect(
																		'select',
																		vm.business_array);

													}

													if ((vm.accessSelArray[0].vertical == 0)
															&& (vm.accessSelArray[0].business == 0)) {
														$("#vertical")
																.multiselect(
																		'selectAll',
																		false);
														$("#vertical")
																.multiselect(
																		'updateButtonText');

													} else {

														$("#vertical")
																.multiselect(
																		'select',
																		vm.vertical_array);

													}

													if (vm.caseTypeInfo.length == vm.caseType_array.length) {
														$("#caseType")
																.multiselect(
																		'selectAll',
																		false);
														$("#caseType")
																.multiselect(
																		'updateButtonText');
													} else {
														$("#caseType")
																.multiselect(
																		'select',
																		vm.caseType_array);

													}
												});

								var modalInstance = $uibModal.open({
									ariaLabelledBy : 'modal-title',
									ariaDescribedBy : 'modal-body',
									templateUrl : 'editUser.html',
									size : 'xl',
									scope : $scope

								});
								modalInstance.result.then(function(response) {
								}, function() {
									console.log('Modal dismissed at: '
											+ new Date());
								});

							},
							function userError(error) {
								alert('There seems some problem. Please try again later...');
								return false;
							});
		}

		function getEmail() {
			vm.form.emailID = vm.form.webMail.replace(/\s+/g, '.')
					+ '@relianceada.com';
		}

		function cancel() {
			$(document).ready(function() {
				$('#company').multiselect('destroy');
				$('#business').multiselect('destroy');
				$('#vertical').multiselect('destroy');
				$('#caseType').multiselect('destroy');
				$uibModalStack.dismissAll();
			})

		}

		function submit(form) {
			angular.forEach(form, function(obj) {
				if (angular.isObject(obj) && angular.isDefined(obj.$setDirty)) {
					obj.$setDirty();
				}
			})

			var comp = '';
			var busi = '';
			var vert = '';
			var caseType = '';

			var b = $('#business').val();
			var c = $('#vertical').val();
			var d = $('#caseType').val();

			var a = $('#company').val();

			if (vm.companyInfo.length == a.length)
				comp = '0';
			else
				comp = a.toString()

			if (vm.businessInfo.length == b.length)
				busi = '0';
			else
				busi = b.toString()

			if (vm.verticalInfo.length == c.length)
				vert = '0';
			else
				vert = c.toString();

			caseType = d.toString();

			var jsonReq = {
				"payrollNo" : form.payrollNo.$modelValue,
				"userName" : form.userName.$modelValue,
				"webMail" : form.webMail.$modelValue,
				"mobileNo" : form.mobileNo.$modelValue,
				"emailID" : form.emailID.$modelValue,
				"userType" : form.userType.$modelValue,
				"accessType" : form.accessType.$modelValue,
				"dob" : form.dob.$modelValue,
				"imeiNo1" : form.imeiNo1.$modelValue,
				"imeiNo2" : form.imeiNo2.$modelValue,
				"imeiNo3" : form.imeiNo3.$modelValue,
				"company" : (comp == "") ? vm.comp_array.toString() : comp,
				"business" : (busi == "") ? vm.business_array.toString() : busi,
				"vertical" : (vert == "") ? vm.vertical_array.toString() : vert,
				"caseType" : (caseType == "") ? vm.caseType_array.toString()
						: caseType,
				"enteredBy" : localStorageService.get('payrollNo'),
				"type" : 'edit',
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}

			// alert(JSON.stringify(jsonReq));
			AddUserService.addNewUser(jsonReq).then(function(result) {

				if (result.data.status == 'T') {
					alert('User details edited successfully');
					$('#company').multiselect('destroy');
					$('#business').multiselect('destroy');
					$('#vertical').multiselect('destroy');
					$('#caseType').multiselect('destroy');
					$uibModalStack.dismissAll();
					$state.reload();
					// $window.location.reload();

				} else {
					alert(result.data.msg);
					return false;
				}
			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});

		}

	}

})();
