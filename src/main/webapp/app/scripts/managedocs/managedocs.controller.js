(function() {
	'use strict';

	angular.module('regCalApp.managedocs').controller('ManageDocsController',
			ManageDocsController);

	ManageDocsController.$inject = [ '$state', 'ManageDocsService', '$scope',
			'localStorageService', 'SearchCaseService', '$filter', '$window',
			'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder',
			'$uibModal', '$uibModalStack', 'usSpinnerService' ];

	/* @ngInject */
	function ManageDocsController($state, ManageDocsService, $scope,
			localStorageService, SearchCaseService, $filter, $window,
			DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $uibModal,
			$uibModalStack, usSpinnerService) {
		var vm = angular.extend(this, {
			caseItem : {},
			dtOptions : {},
			form : {
				docType : '',
				docDesc : '',
				docFile : "",
				docDate : '',
				isConfidential : '',
				isChecked : 'F',
				authUsers : ''
			},
			editform : {
				docType : '',
				docDesc : '',
				docDate : '',
				isConfidential : '',
				authUsers : ''
			},
			docArray : [],
			docTypeInfo : [],
			authUsersInfo : [],
			editDocItem : editDocItem,
			disableDocItem : disableDocItem,
			resetForm : resetForm,
			isEdit : false,
			editDoc : editDoc,
			selItem : {},
			onChangeDate : onChangeDate,
			getCheckBoxVal : getCheckBoxVal,
			goBack : goBack,
			AddNewDoc : AddNewDoc,
			showLoader : showLoader,
			fetchUsers : fetchUsers,
			userList : '',
			fetchEditUsers : fetchEditUsers,
			userEditList : '',
			userName : '',
			showLockModal : showLockModal,
			lockInfo : '',
			extractFilename : extractFilename

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.userID = localStorageService.get('payrollNo');
			vm.userName = localStorageService.get('userName');
			vm.caseDocView = localStorageService.get('caseDocView');
			vm.caseItem = localStorageService.get('addAttachment');
			vm.userType = localStorageService.get('userType');
			vm.caseID = vm.caseItem.caseID;
			vm.company = vm.caseItem.company;
			vm.business = vm.caseItem.business;
			vm.vertical = vm.caseItem.vertical;
			vm.caseType = vm.caseItem.caseType;
			vm.forum = vm.caseItem.forum;
			vm.caseNo = vm.caseItem.caseNo;
			vm.petitioner = vm.caseItem.petitioner;
			vm.respondent = vm.caseItem.respondent;
			vm.subMatter = vm.caseItem.subMatter;

			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'autoWidth', false)

			var jsonReq = {

				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"caseID" : vm.caseID
			}
			ManageDocsService
					.fetchDocTypes(jsonReq)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								vm.docTypeInfo = result.data.docTypeArray;
								vm.authUsersInfo = result.data.authorizedUsersArray;
								$(document)
										.ready(
												function() {

													$('#authUsers')
															.multiselect(
																	{
																		templates : {

																			ul : ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
																			button : '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
																			filter : '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
																			filterClearBtn : '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
																		},
																		buttonContainer : '<div class="dropdown" />',
																		buttonClass : 'btn btn-white',
																		enableFiltering : true,
																		enableCaseInsensitiveFiltering : true,
																		maxHeight : 250,
																		includeSelectAllOption : true,
																		numberDisplayed : 2,
																		nonSelectedText : 'Select Users',
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

								// vm.docTypeInfo.push({
								// 'id' : ' ',
								// 'name' : 'Please choose'
								// })
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});

			var json = {
				"caseID" : vm.caseID,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageDocsService.fetchLmmsDocuments(json).then(function(result) {
				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}
				vm.docArray = result.data.docArray;
			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});

		})();

		function AddNewDoc() {
			//$('#docUploadContainer').collapse('show');
			$('#docUploadContainer').css('display','block');
		}
		
		function extractFilename(s){ 
			  // returns string containing everything from the end of the string 
			  //   that is not a back/forward slash or an empty string on error
			  //   so one can check if return_value===''
			  return (typeof s==='string' && (s=s.match(/[^\\\/]+$/)) && s[0]) || '';
			} 

		function fetchUsers() {
			vm.userList = vm.form.authUsers.toString();
		}
		function fetchEditUsers() {
			vm.userEditList = vm.editform.authUsers.toString();
			$(document)
					.ready(
							function() {

								$('#authUsers')
										.multiselect(
												{
													templates : {

														ul : ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
														button : '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
														filter : '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
														filterClearBtn : '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
													},
													buttonContainer : '<div class="dropdown" />',
													buttonClass : 'btn btn-white',
													enableFiltering : true,
													enableCaseInsensitiveFiltering : true,
													maxHeight : 250,
													includeSelectAllOption : true,
													numberDisplayed : 2,
													nonSelectedText : 'Select Users',
													selectAllValue : 0,
													selectAllName : 'caseType-select',
													onChange : function(
															element, checked) {
														// $('ul.dropdown-menu').removeClass('show');
													},
													onSelectAll : function() {
														$('ul.dropdown-menu')
																.removeClass(
																		'show');
													}

												});
							});
		}

		function editDocItem(val) {

			vm.isEdit = true;
			var item = vm.docArray[val];
			vm.selItem = item;

			vm.editform.docType = item.docType;
			vm.editform.docDesc = item.fileDesc;
			vm.editform.docDate = item.docDate;

			var authArray = item.authUsers.split(',');
			vm.editform.authUsers = authArray

			if (vm.authUsersInfo.length == authArray.length) {
				$("#authUsers").multiselect('selectAll', false);
				$("#authUsers").multiselect('updateButtonText');
			} else {
				$("#authUsers").multiselect('select', authArray);

			}

			if (item.isConfidential == 'T') {
				vm.editform.isConfidential = true;
			} else {
				vm.editform.isConfidential = false;
			}

			$(document)
					.ready(
							function() {

								$('#docDateEdit').datepicker({
									format : 'dd/mm/yyyy',
									todayHighlight : true

								});

								$('#authUsers')
										.multiselect(
												{
													templates : {

														ul : ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
														button : '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
														filter : '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
														filterClearBtn : '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
													},
													buttonContainer : '<div class="dropdown" />',
													buttonClass : 'btn btn-white',
													enableFiltering : true,
													enableCaseInsensitiveFiltering : true,
													maxHeight : 250,
													includeSelectAllOption : true,
													numberDisplayed : 2,
													nonSelectedText : 'Select Users',
													selectAllValue : 0,
													selectAllName : 'caseType-select',
													onChange : function(
															element, checked) {
														// $('ul.dropdown-menu').removeClass('show');
													},
													onSelectAll : function() {
														$('ul.dropdown-menu')
																.removeClass(
																		'show');
													}

												});

							});
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'editDoc.html',
				size : 'md',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

		}

		function resetForm() {
			vm.selItem = {};
			vm.form.docType = '';
			vm.form.docDesc = '';
			vm.form.docFile = '';
			vm.form.isConfidential = 'F';
			$('#authUsers').val('');
			vm.form.docFile = 'F';
			//$('#docUploadContainer').collapse('hide');
			$('#docUploadContainer').css('display','none');

		}

		function showLoader() {
			
				usSpinnerService.spin('spinner-1');
				
		}

		function goBack() {
			$window.close();
		}

		function getCheckBoxVal(item) {
			var val = $('#isConfidential1').prop('checked');
			if (val) {
				vm.form.isChecked = 'T'
				$('#userCtn').css('display', 'block')

			} else {
				vm.form.isChecked = 'F'
					$('#userCtn').css('display', 'none');
			
			}
		}
		
		function showLockModal(authUsers){
			if(authUsers == '')
				return false;
			else
				{
				vm.lockInfo = '';
				vm.lockInfo = '<p>List of authorized users : </p><ol>';
				var spl = authUsers.split(',')
				for(var k =0; k< spl.length;k++){
					vm.lockInfo += '<li>'+spl[k]+'</li>';
				}
				vm.lockInfo += '</ol>';
				//$('#lockInfo').empty();
				//$('#lockInfo').html('Kindly contact the users ' +authUsers)
				var modalInstance = $uibModal.open({
					ariaLabelledBy : 'modal-title',
					ariaDescribedBy : 'modal-body',
					backdrop : 'static',
					keyboard : false,
					templateUrl : 'lock.html',
					size : 'md',
					scope : $scope
				});

				modalInstance.result.then(function(response) {
				}, function() {
					console.log('Modal dismissed at: ' + new Date());
				});
				}
		}

		function onChangeDate() {
			$('#docDate').datepicker('hide');
			var dateArray = $('#docDate').val().split('/');
			var convertedDate = new Date(dateArray[2] + '-' + dateArray[1]
					+ '-' + dateArray[0]);

			var formatted_datetime = $filter('date')(convertedDate,
					'yyyy-MM-dd')
			$("#docDateFormat").val(formatted_datetime);

		}

		function disableDocItem(item) {
			vm.selItem = item;
			if (confirm('Are you sure to delete the document with type "'
					+ item.docTypeName + '" and description "' + item.fileDesc
					+ ' " ')) {
				var jsonReq = {

					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID'),
					"type" : 'delete',
					"docType" : vm.editform.docType,
					"docDesc" : vm.editform.docDesc,
					"docID" : vm.selItem.id,
					"mongoID" : vm.selItem.mongoID,
					"caseID" : vm.caseID
				}
				ManageDocsService
						.manageDocuments(jsonReq)
						.then(
								function(result) {
									if (result.data.status == 'T') {
										alert('Document deleted successfully');
										var json = {
											"caseID" : vm.caseID,
											"userID" : localStorageService
													.get('payrollNo'),
											"sessID" : localStorageService
													.get('sessionID')
										}
										ManageDocsService
												.fetchLmmsDocuments(json)
												.then(
														function(result) {
															if (result.data.status == 'F') {
																alert(result.data.msg);
																return false;
															}
															vm.docArray = result.data.docArray;
														},
														function loginError(
																error) {

															alert('Something went wrong... Pls try again...');
															return false;
														});
									} else {
										alert('Unable to edit document now... Please try again later');
										return false;
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

		function editDoc() {
			var dateArray = $('#docDateEdit').val().split('/');
			var convertedDate = new Date(dateArray[2] + '-' + dateArray[1]
					+ '-' + dateArray[0]);
			var formatted_datetime = $filter('date')(convertedDate,
					'yyyy-MM-dd');
			var checkedVal = 'F'
			var checked = $('#isConfidential2').prop('checked');

			if (checked)
				checkedVal = 'T'
			else
				checkedVal = 'F';
			
			if(checked && vm.userEditList == ''){
				alert('Authorized users list must be selected');
				return false;
			}

			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"type" : 'edit',
				"docType" : vm.editform.docType,
				"docDesc" : vm.editform.docDesc,
				"confidential" : checkedVal,
				"docDate" : formatted_datetime,
				"docID" : vm.selItem.id,
				"authUsers" : vm.userEditList,
				"caseID" : vm.caseID
			}

			ManageDocsService
					.manageDocuments(jsonReq)
					.then(
							function(result) {
								if (result.data.status == 'T') {
									alert('Document details Edited successfully');
									var json = {
										"caseID" : vm.caseID,
										"userID" : localStorageService
												.get('payrollNo'),
										"sessID" : localStorageService
												.get('sessionID')
									}
									ManageDocsService
											.fetchLmmsDocuments(json)
											.then(
													function(result) {
														if (result.data.status == 'F') {
															alert(result.data.msg);
															return false;
														}
														vm.docArray = result.data.docArray;
													},
													function loginError(error) {

														alert('Something went wrong... Pls try again...');
														return false;
													});
								} else {
									alert('Unable to edit document now... Please try again later');
									return false;
								}
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});
			$uibModalStack.dismissAll();
		}
	}

})();
