(function() {
	'use strict';

	angular.module('regCalApp.managenoticedocs').controller(
			'ManageNoticesDocsController', ManageNoticesDocsController);

	ManageNoticesDocsController.$inject = [ '$state',
			'ManageNoticesDocsService', '$scope', 'localStorageService',
			'SearchCaseService', '$filter', '$window', 'DTOptionsBuilder',
			'DTColumnBuilder', 'DTColumnDefBuilder', '$uibModal',
			'$uibModalStack', 'usSpinnerService' ];

	/* @ngInject */
	function ManageNoticesDocsController($state, ManageNoticesDocsService,
			$scope, localStorageService, SearchCaseService, $filter, $window,
			DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $uibModal,
			$uibModalStack, usSpinnerService) {
		var vm = angular.extend(this, {
			noticeItem : {},
			dtOptions : {},
			form : {
				docType : '',
				docDesc : '',
				docFile : "",
				docDate : '',
				isConfidential : '',
				isChecked : 'F'
			},
			editform : {
				docType : '',
				docDesc : '',
				docDate : '',
				isConfidential : ''
			},
			docArray : [],
			docTypeInfo : [],
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
			showLoader : showLoader

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.userID = localStorageService.get('payrollNo');
			vm.noticeDocView = localStorageService.get('noticeDocView');
			vm.noticeItem = localStorageService.get('addNoticeAttachment');
			vm.userType = localStorageService.get('userType');
			vm.noticeID = vm.noticeItem.noticeID;
			vm.company = vm.noticeItem.companyAlias;
			vm.business = vm.noticeItem.businessName;
			vm.vertical = vm.noticeItem.verticalName;
			vm.caseType = vm.noticeItem.caseTypeName;
			vm.noticeSection = vm.noticeItem.noticeSection;
			vm.noticeType = vm.noticeItem.noticeType;
			vm.sectionDesc = vm.noticeItem.sectionDesc;

			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'autoWidth', false)

			var jsonReq = {

				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageNoticesDocsService.fetchDocTypes(jsonReq).then(
					function(result) {
						if(result.data.status == 'F'){
							alert(result.data.msg);
							return false;
						}
						vm.docTypeInfo = result.data.docTypeArray;
						// vm.docTypeInfo.push({
						// 'id' : ' ',
						// 'name' : 'Please choose'
						// })
					}, function loginError(error) {

						alert('Something went wrong... Pls try again...');
						return false;
					});

			var json = {
				"noticeID" : vm.noticeID,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageNoticesDocsService.fetchLmmsDocuments(json).then(
					function(result) {
						if(result.data.status == 'F'){
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
			//$('#docUploadContainerN').collapse('show');
			$('#docUploadContainerN').css('display','block');
		}

		function showLoader() {
			usSpinnerService.spin('spinner-1');
		}
		function editDocItem(val) {

			vm.isEdit = true;
			var item = vm.docArray[val];
			vm.selItem = item;

			vm.editform.docType = item.docType;
			vm.editform.docDesc = item.fileDesc;
			vm.editform.docDate = item.docDate;
			if (item.isConfidential == 'T') {
				vm.editform.isConfidential = true;
			} else {
				vm.editform.isConfidential = false;
			}

			$(document).ready(function() {

				$('#docDateEdit').datepicker({
					format : 'dd/mm/yyyy',
					todayHighlight : true

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
			//$('#docUploadContainerN').collapse('hide');
			$('#docUploadContainerN').css('display','none');

		}

		function goBack() {
			$window.close();

		}

		function getCheckBoxVal(item) {
			var val = $('#isConfidential1').prop('checked');
			if (val)
				vm.form.isChecked = 'T'
			else
				vm.form.isChecked = 'F'
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
					"mongoID" : vm.selItem.mongoID
				}
				ManageNoticesDocsService
						.manageDocuments(jsonReq)
						.then(
								function(result) {
									if (result.data.status == 'T') {
										alert('Document deleted successfully');
										var json = {
											"noticeID" : vm.noticeID,
											"userID" : localStorageService
													.get('payrollNo'),
											"sessID" : localStorageService
													.get('sessionID')
										}
										ManageNoticesDocsService
												.fetchLmmsDocuments(json)
												.then(
														function(result) {
															if(result.data.status == 'F'){
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

			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"type" : 'edit',
				"docType" : vm.editform.docType,
				"docDesc" : vm.editform.docDesc,
				"confidential" : checkedVal,
				"docDate" : formatted_datetime,
				"docID" : vm.selItem.id
			}

			ManageNoticesDocsService
					.manageDocuments(jsonReq)
					.then(
							function(result) {
								if (result.data.status == 'T') {
									alert('Document details Edited successfully');
									var json = {
										"noticeID" : vm.noticeID,
										"userID" : localStorageService
												.get('payrollNo'),
										"sessID" : localStorageService
												.get('sessionID')
									}
									ManageNoticesDocsService
											.fetchLmmsDocuments(json)
											.then(
													function(result) {
														if(result.data.status == 'F'){
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
