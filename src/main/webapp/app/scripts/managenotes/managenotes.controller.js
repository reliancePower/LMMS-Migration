(function() {
	'use strict';

	angular.module('regCalApp.managenotes').controller('ManageNotesController',
			ManageNotesController);

	ManageNotesController.$inject = [ '$state', 'ManageNotesService', '$scope',
			'localStorageService', 'SearchCaseService', '$filter', '$window',
			'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder',
			'$uibModal', '$uibModalStack', 'usSpinnerService' ];

	/* @ngInject */
	function ManageNotesController($state, ManageNotesService, $scope,
			localStorageService, SearchCaseService, $filter, $window,
			DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $uibModal,
			$uibModalStack, usSpinnerService) {
		var vm = angular.extend(this, {
			caseItem : {},
			dtOptions : {},
			AddNewNotes : AddNewNotes,
			resetForm : resetForm,
			goBack : goBack,
			authUsersInfo : [],
			notesArray : [],
			emailArray : [],
			mailTemplateHTML : '',
			isHOD : false,
			toMail : '',
			ccMail : '',
			sendDraft : sendDraft,
			viewNoteItem : viewNoteItem,
			editNoteItem : editNoteItem,
			editItem : '',
			editMailTemplateHTML : '',
			editDraft : editDraft,
			disableNoteItem : disableNoteItem,
			approveNote : approveNote,
			tempSelect : '',
			fetchTemplateDetails : fetchTemplateDetails,
			isUser : true,
			hodName : '',
			hodEmail : '',
			previewNoteItem : previewNoteItem,
			finalPreviewNoteItem : finalPreviewNoteItem,
			sendNote : sendNote,
			approveNoteItem : approveNoteItem,
			printViewNote : printViewNote,
			hodEditDraft : hodEditDraft,
			confirmNote : confirmNote,
			moreRecipients : ''

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
			vm.caseItem = localStorageService.get('managenotes');
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
			vm.hearingUp = '';

			var date = new Date();
			var day1 = ("0" + date.getDate()).slice(-2);
			var month1 = ("0" + (date.getMonth() + 1)).slice(-2);
			vm.days = (day1) + '/' + (month1) + '/' + date.getFullYear()

			if (vm.caseItem.statusID == 5 || vm.caseItem.statusID == 23
					|| vm.caseItem.statusID == 24)
				vm.hearingUp = 'Final Outcome';
			else
				vm.hearingUp = 'Hearing Update';

			var sp = vm.caseItem.petitioners.split("P2.");
			vm.pet = sp[0].substring(3, sp[0].length)
					+ ((vm.caseItem.petitioners.indexOf("P2.") > -1) ? " and Ors,."
							: "");
			var rsp = vm.caseItem.respondents.split("R2.");
			vm.resp = rsp[0].substring(3, rsp[0].length)
					+ ((vm.caseItem.respondents.indexOf("R2.") > -1) ? " and Ors,."
							: "");

			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'autoWidth', false)

			var jsonReq = {

				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"caseID" : parseInt(vm.caseID),
				"companyID" : parseInt(vm.caseItem.companyID),
				"type" : "fetchMaster"
			}
			ManageNotesService.manageNotes(jsonReq).then(function(result) {
				if (result.data.status == 'S') {
					alert(result.data.msg);
					$window.localStorage.clear();
					localStorageService.clearAll;
					localStorageService.cookie.clearAll;
					$state.go('login');

				}
				vm.emailArray = result.data.emailArray;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});

			var json = {
				"caseID" : parseInt(vm.caseID),
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"type" : "fetchNotes"
			}
			ManageNotesService.manageNotes(json).then(function(result) {
				if (result.data.status == 'S') {
					// alert(result.data.msg);
					return false;
				}
				vm.notesArray = result.data.notesArray;
			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});

			vm.dtColumnDefs = [
					DTColumnDefBuilder.newColumnDef(0).notVisible(),
					DTColumnDefBuilder.newColumnDef(1).notVisible(),
					DTColumnDefBuilder.newColumnDef(2).notVisible(),
					DTColumnDefBuilder.newColumnDef(3).notVisible(),
					DTColumnDefBuilder.newColumnDef(4).notVisible(),
					DTColumnDefBuilder.newColumnDef(5).notVisible(),
					DTColumnDefBuilder.newColumnDef(6).notVisible()

			];

			vm.dtOptions = DTOptionsBuilder
					.newOptions()
					.withPaginationType('full_numbers')
					.withOption('order', [])
					.withOption('autoWidth', true)
					.withOption('deferRender', true)
					.withButtons(
							[

									{
										extend : "excelHtml5",
										fileName : "List of Notes",
										title : "Law Matters Management Systems - List of Notes",
										autoFilter : true,
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14 ]
										},

										exportData : {
											decodeEntities : true
										}
									},
									{
										extend : "pdfHtml5",
										orientation : 'landscape',
										pageSize : 'A3',
										fileName : "List of Notes",
										title : "Law Matters Management Systems - List of Notes",

										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14 ]
										},
										header : true,

										exportData : {
											decodeEntities : true
										}

									},
									{
										extend : "print",

										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14 ]
										},

										autoPrint : true,
										title : "List of Notes",

									}

							]);

		})();

		function AddNewNotes() {

			$(document).ready(function() {
				$('#MailContainer').css('display', 'block');
			});
		}

		function confirmNote() {
			if (vm.isHOD) {
				sendDraft();

			} else {

				if (confirm('Do you want to copy this mail to any other person?')) {

					var modalInstance = $uibModal.open({
						ariaLabelledBy : 'modal-title',
						ariaDescribedBy : 'modal-body',
						backdrop : 'static',
						keyboard : false,
						templateUrl : 'addRecip.html',
						size : 'lg',
						scope : $scope
					});

					modalInstance.result.then(function(response) {
					}, function() {
						console.log('Modal dismissed at: ' + new Date());
					});
				} else
					sendDraft();
			}
		}

		function fetchTemplateDetails(item) {

			var tName = _.where(vm.emailArray, {
				id : item
			});
			vm.tempSelectName = tName[0].name;

			SearchCaseService
					.viewCase(vm.caseItem.caseID)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								vm.caseItem = result.data;
								localStorageService.set('managenotes',
										result.data)

							},
							function caseError(error) {
								alert('There seems some problem. Please try again later...');
								return false;
							});

			var jsonReq = {

				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"caseID" : parseInt(vm.caseID),
				"id" : parseInt(item),
				"type" : "fetchTemplate"
			}

			ManageNotesService
					.manageNotes(jsonReq)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}

								vm.authUsersInfo = result.data.resultArray;

								if ((_.contains(
										vm.authUsersInfo[0].HODPayrollNo
												.split(', '), vm.userID))
										|| (vm.authUsersInfo[0].HODPayrollNo
												.trim() == vm.userID))
									vm.isHOD = true;
								else
									vm.isHOD = false;

								vm.toMail = vm.authUsersInfo[0].toMail;
								vm.toMail = vm.toMail.replace(',', ', ');

								vm.ccMail = vm.authUsersInfo[0].ccMail;
								vm.ccMail = vm.ccMail.replace(',', ', ');
								vm.hodEmail = vm.authUsersInfo[0].HODEmailIDS;
								vm.hodName = vm.authUsersInfo[0].HODsName;

								vm.mailSubject = vm.caseItem.vertical
										+ ' Matter - ' + vm.hearingUp;

								vm.mailTemplateHTML = $('#mailAppend').html();

								$('#mTemp').css('display', 'block');
								$('#txtAngID  > div.ta-editor > div.ta-bind')
										.attr('contenteditable', 'false');
								
								$('#txtAngID > div.ta-editor > div.ta-bind > table').css('border-collapse','collapse');

							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});
		}

		function sendDraft() {
			$uibModalStack.dismissAll();

			var json = {
				"caseID" : parseInt(vm.caseID),
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"ccMail" : vm.ccMail,
				"toMail" : vm.toMail,
				"hodEmail" : vm.hodEmail,
				"id" : vm.tempSelect,
				"finImpOneTime" : $('#finImpOneTime').text(),
				"finImpRecurring" : $('#finImpRecurring').text(),
				"finImaRecurringDuration" : $('#finImaRecurringDuration')
						.text(),
				"subjMatter" : $('#subjMatter').text(),
				"briefFacts" : $('#briefFacts').text(),
				"additionalComments" : $('#additionalComments').text(),
				"mailSub" : vm.mailSubject,
				"template" : vm.mailTemplateHTML,
				"type" : "saveDraft",
				"fromEmail" : localStorageService.get('emailID'),
				"draftByCCMail" : vm.moreRecipients,
				"isHOD" : vm.isHOD
			}

			ManageNotesService
					.manageNotes(json)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								if (result.data.count > 0) {

									if (vm.isHOD)
										alert('Your template has been saved as draft.');

									else
										alert('Your template has been sent for approval suceessfully.');
									$(document).ready(
											function() {
												$('#MailContainer').css(
														'display', 'none');
											});
									$state.reload();
								}
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});

		}

		function editDraft() {

			var json = {
				"caseID" : parseInt(vm.caseID),
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"ccMail" : vm.headerEditCC,
				"toMail" : vm.headerEditTo,
				"hodEmail" : vm.hodEmailE,
				"finImpOneTime" : $('#finImpOneTimeE').text(),
				"finImpRecurring" : $('#finImpRecurringE').text(),
				"finImaRecurringDuration" : $('#finImaRecurringDurationE')
						.text(),
				"subjMatter" : $('#subjMatterE').text(),
				"briefFacts" : $('#briefFactsE').text(),
				"additionalComments" : $('#additionalCommentsE').text(),
				"mailSub" : vm.headerEditSubject,
				"template" : vm.editMailTemplateHTML,
				"type" : "editDraft",
				"id" : vm.editItem.id,
				"draftByCCMail" : vm.editItem.draftByCCMail,
				"fromEmail" : localStorageService.get('emailID')
			}
			ManageNotesService.manageNotes(json).then(
					function(result) {
						if (result.data.status == 'F') {
							alert(result.data.msg);
							return false;
						}
						if (result.data.count > 0) {
							alert('Your note for Case ID ' + vm.caseItem.caseID
									+ ' with Communication Reference No '
									+ result.data.crNo
									+ ' has been updated successfully.');

							$state.reload();
						}
					}, function loginError(error) {

						alert('Something went wrong... Pls try again...');
						return false;
					});
		}

		function hodEditDraft() {
			var json = {
				"caseID" : parseInt(vm.caseID),
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"ccMail" : vm.headerEditCC,
				"toMail" : vm.headerEditTo,
				"hodEmail" : vm.hodEmailE,
				"finImpOneTime" : $('#finImpOneTimeE').text(),
				"finImpRecurring" : $('#finImpRecurringE').text(),
				"finImaRecurringDuration" : $('#finImaRecurringDurationE')
						.text(),
				"subjMatter" : $('#subjMatterE').text(),
				"briefFacts" : $('#briefFactsE').text(),
				"additionalComments" : $('#additionalCommentsE').text(),
				"mailSub" : vm.headerEditSubject,
				"template" : vm.editMailTemplateHTML,
				"type" : "approveNotes",
				"id" : vm.editItem.id,
				"fromEmail" : localStorageService.get('emailID')
			}
			ManageNotesService
					.manageNotes(json)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								if (result.data.count > 0) {
									alert('Case Update note of Case ID '
											+ vm.caseItem.caseID
											+ ' has been sent suceessfully with Communication Reference No : '
											+ result.data.crNo);
									$state.reload();
								}
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});
		}

		function sendNote() {
			
			
			$uibModalStack.dismissAll();
			var json = {
				"caseID" : parseInt(vm.caseID),
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"fromEmail" : localStorageService.get('emailID'),
				"ccMail" : vm.ccMail,
				"toMail" : vm.toMail,
				"hodEmail" : vm.hodEmail,
				"id" : vm.tempSelect,
				"finImpOneTime" : $('#finImpOneTime').text(),
				"finImpRecurring" : $('#finImpRecurring').text(),
				"finImaRecurringDuration" : $('#finImaRecurringDuration')
						.text(),
				"subjMatter" : $('#subjMatter').text(),
				"briefFacts" : $('#briefFacts').text(),
				"additionalComments" : $('#additionalComments').text(),
				"mailSub" : vm.mailSubject,
				"template" : vm.mailTemplateHTML,
				"draftByEmail" : localStorageService.get('emailID'),
				"type" : "submitNotes"
			}

			ManageNotesService
					.manageNotes(json)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								if (result.data.count > 0) {
									alert('Case Update note of Case ID '
											+ vm.caseItem.caseID
											+ ' has been sent suceessfully with Communication Reference No : '
											+ result.data.crNo);
									$(document).ready(
											function() {
												$('#MailContainer').css(
														'display', 'none');
											});
									$state.reload();
								}
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});

		}

		function finalPreviewNoteItem() {
			vm.finalPreviewNoteMailTemplate = vm.mailTemplateHTML;
			vm.headerFinalPreviewTemplate = vm.tempSelectName;
			vm.headerFinalPreviewSubject = vm.mailSubject;
			vm.headerFinalPreviewTo = vm.toMail;
			vm.headerFinalPreviewCC = vm.ccMail;

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'finalPreviewNote.html',
				size : 'xl',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function previewNoteItem() {

			vm.previewNoteMailTemplate = vm.mailTemplateHTML;
			vm.headerPreviewTemplate = vm.tempSelectName;
			vm.headerPreviewSubject = vm.mailSubject;
			vm.headerPreviewTo = vm.toMail;
			vm.headerPreviewCC = vm.ccMail;

			$(document).ready(function() {
				$('#emailTempPreview span').attr('contenteditable', 'false');
				$('#emailTempPreview ol').attr('contenteditable', 'false');
			});

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'previewNote.html',
				size : 'xl',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

		}

		function printViewNote() {

			var title = '<img  style = "width: 200px;height: auto;padding: 16px 15px 0px 10px;" src = "app/img/logo.png" /><h5 style = "display: inline-block;padding-left: 50px;color : #005199 !important;font-weight:600;">'
					+ $('#titleText').text() + '</h5><hr>';
			var w = window.open();
			w.document.write($("#print-header").html() + title
					+ $("#viewNoteMatter").html() + $("#print-footer").html());
			w.document.close();
			w.focus();
		}

		function viewNoteItem(val) {

			vm.viewNoteMailTemplate = val.mailTemplate;

			vm.headerTemplate = val.templateName;
			vm.headerSubject = val.subject;
			vm.headerTo = val.toMail;
			vm.headerCC = val.ccMail;

			var no = val.crNo.split('-');

			vm.footerInfo = 'Case ID - ' + vm.caseItem.caseID
					+ '/ Communication Ref. No - ' + val.crNo + ' dated '
					+ vm.days;
			$(document).ready(function() {
				$('#emailTemp span').attr('contenteditable', 'false');
				$('#emailTemp ol').attr('contenteditable', 'false');
			});

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				backdrop : 'static',
				keyboard : false,
				templateUrl : 'viewNote.html',
				size : 'xl',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

		}

		function approveNoteItem(val) {

			SearchCaseService
					.viewCase(vm.caseItem.caseID)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								vm.caseItem = result.data;
								localStorageService.set('managenotes',
										result.data)
								vm.approveItem = val;

								var temp = val.mailTemplate;

								vm.headerFPreviewTemplate = val.templateName;
								vm.headerFPreviewSubject = val.subject;
								vm.headerFPreviewTo = val.toMail;
								vm.headerFPreviewCC = val.ccMail;

								vm.fPreviewMailTemplate = temp;

								$(document)
										.ready(
												function() {
													$('#userName').text(
															vm.userName);

													$(
															'#emailTempFPreview #finImpOneTime')
															.attr('id',
																	'finImpOneTimeA');
													$(
															'#emailTempFPreview span#finImpRecurring')
															.attr('id',
																	'finImpRecurringA');
													$(
															'#emailTempFPreview span#finImaRecurringDuration')
															.attr('id',
																	'finImaRecurringDurationA');
													$(
															'#emailTempFPreview span#subjMatter')
															.attr('id',
																	'subjMatterA');
													$(
															'#emailTempFPreview #briefFacts')
															.attr('id',
																	'briefFactsA');

													$(
															'#emailTempFPreview #finImpOneTimeE')
															.attr('id',
																	'finImpOneTimeA');
													$(
															'#emailTempFPreview #finImpRecurringE')
															.attr('id',
																	'finImpRecurringA');
													$(
															'#emailTempFPreview #finImaRecurringDurationE')
															.attr('id',
																	'finImaRecurringDurationA');
													$(
															'#emailTempFPreview #subjMatterE')
															.attr('id',
																	'subjMatterA');
													$(
															'#emailTempFPreview #briefFactsE')
															.attr('id',
																	'briefFactsA');

													$(
															'#emailTempFPreview #finImpOneTimeA')
															.text(
																	vm.caseItem.finImpact);
													$(
															'#emailTempFPreview #finImpRecurringA')
															.text(
																	vm.caseItem.finImpRecurring);
													$(
															'#emailTempFPreview #finImaRecurringDurationA')
															.text(
																	vm.caseItem.finImaRecurringDuration);
													$(
															'#emailTempFPreview #subjMatterA')
															.text(
																	vm.caseItem.subMatter);
													$(
															'#emailTempFPreview #briefFactsA')
															.text(
																	vm.caseItem.briefFacts);

												});

								var modalInstance = $uibModal
										.open({
											ariaLabelledBy : 'modal-title',
											ariaDescribedBy : 'modal-body',
											backdrop : 'static',
											keyboard : false,
											templateUrl : 'finalPreviewApproveNote.html',
											size : 'xl',
											scope : $scope
										});

								modalInstance.result.then(function(response) {
								}, function() {
									console.log('Modal dismissed at: '
											+ new Date());
								});

							},
							function caseError(error) {
								alert('There seems some problem. Please try again later...');
								return false;
							});

		}

		function approveNote() {
			var json = {
				"caseID" : parseInt(vm.caseID),
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"ccMail" : vm.headerFPreviewCC,
				"toMail" : vm.headerFPreviewTo,
				"mailSub" : vm.headerFPreviewSubject,
				"template" : $('#emailTempFPreview').html(),
				"finImpOneTime" : $('#finImpOneTimeA').text(),
				"finImpRecurring" : $('#finImpRecurringA').text(),
				"finImaRecurringDuration" : $('#finImaRecurringDurationA')
						.text(),
				"subjMatter" : $('#subjMatterA').text(),
				"briefFacts" : $('#briefFactsA').text(),
				"additionalComments" : $('#additionalCommentsA').text(),
				"fromEmail" : localStorageService.get('emailID'),
				"id" : vm.approveItem.id,
				"draftByEmail" : vm.approveItem.draftByEmail,
				"type" : "approveNotes"
			}
			ManageNotesService
					.manageNotes(json)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								if (result.data.count > 0) {
									alert('Case Update note of Case ID '
											+ vm.caseItem.caseID
											+ ' has been sent suceessfully with Communication Reference No : '
											+ result.data.crNo);
									$(document).ready(
											function() {
												$('#MailContainer').css(
														'display', 'none');
											});
									$state.reload();
								}
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});
		}

		function editNoteItem(val) {

			SearchCaseService
					.viewCase(vm.caseItem.caseID)
					.then(
							function(result) {
								if (result.data.status == 'F') {
									alert(result.data.msg);
									return false;
								}
								vm.caseItem = result.data;
								localStorageService.set('managenotes',
										result.data)

								vm.editItem = val;
								var temp = val.mailTemplate;
								vm.hodEmailE = val.HODEmailIDS;
								vm.headerEditTemplate = val.templateName;
								vm.headerEditSubject = val.subject;
								vm.headerEditTo = val.toMail;
								vm.headerEditCC = val.ccMail;

								vm.editMailTemplateHTML = temp;

								$(document)
										.ready(
												function() {

													$(
															'div#editTemplateNote  > div.ta-editor > div.ta-bind')
															.attr(
																	'contenteditable',
																	'false');

													$(
															'#editTemplateNote #finImpOneTime')
															.attr('id',
																	'finImpOneTimeE');
													$(
															'#editTemplateNote #finImpRecurring')
															.attr('id',
																	'finImpRecurringE');
													$(
															'#editTemplateNote #finImaRecurringDuration')
															.attr('id',
																	'finImaRecurringDurationE');
													$(
															'#editTemplateNote #subjMatter')
															.attr('id',
																	'subjMatterE');
													$(
															'#editTemplateNote #briefFacts')
															.attr('id',
																	'briefFactsE');

													$('#finImpOneTimeE')
															.text(
																	vm.caseItem.finImpact);
													$('#finImpRecurringE')
															.text(
																	vm.caseItem.finImpRecurring);
													$(
															'#finImaRecurringDurationE')
															.text(
																	vm.caseItem.finImaRecurringDuration);
													$('#subjMatterE')
															.text(
																	vm.caseItem.subMatter);
													$('#briefFactsE')
															.text(
																	vm.caseItem.briefFacts);

												});

								var modalInstance = $uibModal.open({
									ariaLabelledBy : 'modal-title',
									ariaDescribedBy : 'modal-body',
									backdrop : 'static',
									keyboard : false,
									templateUrl : 'editNote.html',
									size : 'xl',
									scope : $scope
								});

								modalInstance.result.then(function(response) {
								}, function() {
									console.log('Modal dismissed at: '
											+ new Date());
								});

							},
							function caseError(error) {
								alert('There seems some problem. Please try again later...');
								return false;
							});

		}

		function disableNoteItem(val) {
			if (confirm('Are you sure to delete this note')) {
				var json = {
					"caseID" : parseInt(vm.caseID),
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID'),
					"type" : "deleteNote",
					"id" : val.id
				}
				ManageNotesService.manageNotes(json).then(function(result) {
					if (result.data.status == 'F') {
						alert(result.data.msg);
						return false;
					}
					if (result.data.count > 0) {
						alert('Your case note has been deleted successfully.');
						$(document).ready(function() {
							$('#MailContainer').css('display', 'none');
						});
						$state.reload();
					}
				}, function loginError(error) {

					alert('Something went wrong... Pls try again...');
					return false;
				});
			} else
				return false;
		}

		function resetForm() {
			$(document).ready(function() {
				$('#MailContainer').css('display', 'none');
			});
		}

		function goBack() {
			$window.close();
		}

	}

})();
