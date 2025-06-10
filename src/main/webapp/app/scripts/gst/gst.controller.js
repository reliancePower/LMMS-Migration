(function() {
	'use strict';

	angular.module('regCalApp.gst').controller('GstController',
		GstController);

	GstController.$inject = ['$state', 'viewCaseService',
		'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
		'DTColumnDefBuilder', '$scope', '$uibModal', '$uibModalStack',
		'$compile', '$window', '$filter', 'dashboardService',
		'AddUserService', 'addCaseService', 'DTInstances',
		'GstService', 'AddNoticeService', 'ManageNoticesDocsService', 'navbarService'];

	/* @ngInject */
	function GstController($state, NoticeService, localStorageService,
		DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $scope,
		$uibModal, $uibModalStack, $compile, $window, $filter,
		dashboardService, AddUserService, addCaseService, DTInstances,
		GstService, AddNoticeService, ManageNoticesDocsService, navbarService) {
		var vm = angular.extend(this, {
			submit: submit,
			fetchAllMaster: fetchAllMaster,
			form: {
				company: '',
				business: '',
				vertical: '',
				caseType: '',
				state: 0,
				panNo: '',
				assessYear: '',
				noticeType: '',
				section: 0,
				sectionDesc: '',
				addressedTo: '',
				issuedByName: '',
				issuedByDesig: '',
				issuedByOrganization: '',
				natureOfParty: '',
				briefFacts: '',
				stakeInvolment: '',
				dateOnNotice: '',
				dateOnWhichNoticeReceived: '',
				dateOnWhichNoticeRequiredToReply: '',
				dateOfReply: '',
				remarks: '',
				status: 0,
				priority: 0
			},
			companyInfo: [],
			businessInfo: [],
			verticalInfo: [],
			caseTypeInfo: [],
			gstTypeInfo: [],
			gstSectionInfo: [],
			gstStatusInfo: [],
			stateInfo: [],
			priorityInfo: [],
			noticeTypeInfo: [],
			noticeStatusInfo: [],
			sectionInfo: [],
			years: [],
			checkYear: checkYear,
			yearSel: false,
			viewAdditionalNoticeItem:viewAdditionalNoticeItem,
			fetchVerticalMaster: fetchVerticalMaster,
			fetchCompanyMaster: fetchCompanyMaster,
			selComp: '',
			fetchNoticeSection: fetchNoticeSection,
			fetchSectionDesc: fetchSectionDesc,
			selNoticyType: '',
			noticesInfo: [],
			fetchAllNotices: fetchAllNotices,
			accessCtrl: '',
			updateNotice: updateNotice,
			noticeInfo: '',
			noticeID: '',
			viewNoticeItem: viewNoticeItem,
			today: new Date(),
			printTable: printTable,
			addDocItem: addDocItem,
			paperArray: [],
			search: search,
			resetFilter: resetFilter

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
				|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.userID = localStorageService.get('payrollNo');
			vm.noticeDocView = localStorageService.get('noticeDocView');
			vm.accessCtrl = localStorageService.get('accessCtrl');
			vm.userType = localStorageService.get('userType');
			// fetchAllNotices();
			fetchAllMaster();
			localStorageService.remove('searchCaseDetails');
			var d = new Date();
			var n = d.getFullYear();
			var next = n + 1;
			for (var i = 1990; i <= next; i++) {
				var t = i + 1;
				vm.years.push(i + '-' + t);
			}

			vm.dtColumnDefs = [
				DTColumnDefBuilder.newColumnDef(1).notVisible(),
				DTColumnDefBuilder.newColumnDef(2).notVisible(),
				DTColumnDefBuilder.newColumnDef(5).notVisible(),
				DTColumnDefBuilder.newColumnDef(6).notVisible(),
				DTColumnDefBuilder.newColumnDef(9).notVisible(),
				DTColumnDefBuilder.newColumnDef(10).notVisible(),
				DTColumnDefBuilder.newColumnDef(15).notVisible(),
				DTColumnDefBuilder.newColumnDef(16).notVisible(),
				DTColumnDefBuilder.newColumnDef(18).notVisible(),
				DTColumnDefBuilder.newColumnDef(20).notVisible(),
				DTColumnDefBuilder.newColumnDef(21).notVisible(),
				DTColumnDefBuilder.newColumnDef(22).notVisible(),
				DTColumnDefBuilder.newColumnDef(23).notVisible(),
				DTColumnDefBuilder.newColumnDef(24).notVisible(),

				DTColumnDefBuilder.newColumnDef([11, 12, 13, 14])
					.withOption('type', 'date-euro')];

			vm.dtOptions = DTOptionsBuilder
				.newOptions()
				.withPaginationType('full_numbers')
				.withOption('order', [])
				.withOption('autoWidth', true)
				.withOption('deferRender', true)
				.withButtons(
					[
						{
							extend: "excelHtml5",
							fileName: "List of Notices",
							title: "Law Matters Management Systems - List of Notices",
							autoFilter: true,
							exportOptions: {
								format: {
									body: function(data, row,
										column, node) {
										if (node.id == 'noticeID') {
											return node.textContent
												.trim();
										} else
											return data;
									}
								},

								columns: [0, 1, 2, 3, 4, 5, 6, 7,
									8, 9, 10, 11, 12, 13, 14,
									15, 16, 17, 18, 19, 20, 21,
									22, 23, 24]
							},
							text: 'Excel Master Data',

							exportData: {
								decodeEntities: true
							}
						},
						{
							extend: "colvis",
							columns: ':not(.noVis)',
							text: "Show/Hide Columns"

						},

						{
							extend: "excelHtml5",
							fileName: "List of Notices",
							title: "Law Matters Management Systems - List of Notices",
							autoFilter: true,
							exportOptions: {

								format: {
									body: function(data, row,
										column, node) {
										if (node.id == 'noticeID') {
											return node.textContent
												.trim();
										} else
											return data;
									}
								},
								columns: ':visible:not(:last-child)'
							},

							exportData: {
								decodeEntities: true
							}
						},
						{
							extend: "pdfHtml5",
							orientation: 'landscape',
							pageSize: 'A2',
							fileName: "List of Notices",
							title: "Law Matters Management Systems - List of Notices",

							exportOptions: {

								format: {
									body: function(data, row,
										column, node) {
										if (node.id == 'noticeID') {
											return node.textContent
												.trim();
										} else
											return data;
									}
								},
								columns: ':visible:not(:last-child)'
							},
							header: true,

							customize: function(doc, ev) {
								var cols = doc.content[1].table.body[0].length;
								if (cols >= 15) {
									alert('Only 14 columns are allowed to make PDF.');

									ev.stopPropagation();
									return false;
								}

							},
							exportData: {
								decodeEntities: true
							}

						},
						{
							extend: "print",

							exportOptions: {

								columns: ':visible:not(:last-child)'
							},

							autoPrint: true,
							title: "List of Notices",
							customize: function(doc, ev) {
								$.fn.columnCount = function() {
									return $('th', $(this).find(
										'thead')).length;
								};

								var colctr = $('#searchNoticeTable')
									.columnCount();

								if (colctr >= 15) {
									alert('Only 14 columns are allowed to Print.');
									doc.close();
									ev.stopPropagation();
									return false;
								} else {

									var css = '@page { size: landscape; }', head = doc.document.head
										|| doc.document
											.getElementsByTagName('head')[0], style = doc.document
												.createElement('style');

									style.type = 'text/css';
									style.media = 'print';

									if (style.styleSheet) {
										style.styleSheet.cssText = css;
									} else {
										style
											.appendChild(doc.document
												.createTextNode(css));
									}

									head.appendChild(style);
								}

							}
						}

					]);

			// vm.dtOptions = DTOptionsBuilder.newOptions()
			// .withPaginationType('full_numbers')
			// .withOption('order', [])
			//		          
			// .withButtons([
			// {
			// extend: "excelHtml5",
			// fileName: "List of Notices",
			// title:"Law Matters Management Systems - List of Notices",
			// exportOptions: {
			// columns: ':visible'
			// },
			// messageTop: "Law Matters Management Systems - List of Notices",
			// exportData: { decodeEntities: true }
			// },
			// {
			// extend: "pdfHtml5",
			// pageSize: 'A2',
			// fileName: "List of Cases",
			// title:"Law Matters Management Systems - List of Notices",
			// exportOptions: {
			// columns: ':visible'
			// },
			// exportData: {decodeEntities:true}
			//				    
			// },{
			// extend: "print",
			// exportOptions: {
			// columns: ':visible'
			// },
			// autoPrint : true,
			// title:"List of Notices"
			//				        
			//				    
			// }]);

		})();

		function resetFilter() {
			$('.mulSel').multiselect('deselectAll', false);
			$('.mulSel').multiselect('refresh');
			$('#form_startDate').val('');
			$('#form_endDate').val('');
			$('#form_dateOnWhichNoticeRequiredToReply').val('');
			vm.form.startDate = '';
			vm.form.endDate = '';
			vm.form.form_dateOnWhichNoticeRequiredToReply = '';
			localStorageService.set('searchNoticeDetails', {});

		}

		function checkYear() {
			var sel = $('#caseType').val();
			if (parseInt(sel) == 1) {
				$('#hiddenContainer1').css('display', 'block');
				$('#hiddenContainer2').css('display', 'block');
				$('#hiddenContainer3').css('display', 'block');
				$('#hiddenContainer4').css('display', 'block');
				$('#hiddenContainer5').css('display', 'block');
				$('#typeContainer').css('display', 'none');
				vm.yearSel = true;
			} else {
				$('#hiddenContainer1').css('display', 'none');
				$('#hiddenContainer2').css('display', 'none');
				$('#hiddenContainer3').css('display', 'none');
				$('#hiddenContainer4').css('display', 'none');
				$('#hiddenContainer5').css('display', 'none');
				$('#typeContainer').css('display', 'block');
				vm.yearSel = false;
			}

		}

		function printTable() {
			// $('#viewTable').DataTable().destroy()
			var title = '<img  style = "width: 200px;height: auto;padding: 16px 15px 0px 10px;" src = "app/img/logo.png" /><h5 style = "display: inline-block;padding-left: 50px;color : #005199 !important;font-weight:600;">'
				+ $('#textTitle').text() + '</h5><hr>';
			var w = window.open();
			w.document.write($("#print-header").html() + title
				+ $("#viewAllMatter").html() + $("#print-footer").html());
			w.document.close();
			w.focus();
		}

		function addDocItem(val) {
			localStorageService.set('addNoticeAttachment', val);
			$state.goToNewTab('managenoticedocs', {});
		}

		function viewNoticeItem(item) {
			var docId = item.docId;

			// This should point to a REST endpoint that handles file download
			var downloadUrl = "./rest/V2/downloadPdf?docId=" + encodeURIComponent(docId);

			// Trigger download
			window.open(downloadUrl, "_blank");
		}


		function fetchAllNotices() {
			GstService.fetchAllNotices().then(function(result) {
				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}
				vm.noticesInfo = result.data.noticesMaster;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function updateNotice(item) {

			fetchCompanyMaster(item.company);
			fetchVerticalMaster(item.business);
			fetchNoticeSection(item.noticeType);

			vm.noticeID = item.noticeID;
			vm.form.company = item.company;
			vm.form.business = item.business;
			vm.form.vertical = item.vertical.toString();
			vm.form.caseType = item.caseType.toString();
			vm.form.panNo = item.panNo;
			vm.form.assessYear = item.assessYear;
			vm.form.noticeType = item.noticeType;
			vm.form.section = item.noticeMasterID;
			vm.form.sectionDesc = item.sectionDesc;
			vm.form.dateOnNotice = $filter('date')(item.dateOnNotice,
				'dd/MM/yyyy');
			vm.form.dateOnWhichNoticeReceived = $filter('date')(
				item.dateOnWhichNoticeReceived, 'dd/MM/yyyy');
			vm.form.dateOnWhichNoticeRequiredToReply = $filter('date')(
				item.dateOnWhichNoticeRequiredToReply, 'dd/MM/yyyy');
			vm.form.dateOfReply = $filter('date')(item.dateOfReply,
				'dd/MM/yyyy');
			vm.form.state = item.state.toString();
			vm.form.issuedByName = item.issuedByName;
			vm.form.issuedByDesig = item.issuedByDesig;
			vm.form.issuedByOrganization = item.issuedByOrganization;
			vm.form.addressedTo = item.addressedTo;
			vm.form.natureOfParty = item.natureOfParty;
			vm.form.briefFacts = item.briefFacts;
			vm.form.stakeInvolment = item.stakeInvolment;
			vm.form.priority = item.priority.toString();
			;
			vm.form.remarks = item.remarks;
			vm.form.status = parseInt(item.statusID);

			$(document)
				.ready(
					function() {
						if (item.caseType == 1) {
							$('#hiddenContainer1').css('display',
								'block');
							$('#hiddenContainer2').css('display',
								'block');
							$('#hiddenContainer3').css('display',
								'block');
							$('#hiddenContainer4').css('display',
								'block');
							$('#hiddenContainer5').css('display',
								'block');
							$('#typeContainer').css('display', 'none');
							vm.yearSel = true;
						} else {
							$('#hiddenContainer1').css('display',
								'none');
							$('#hiddenContainer2').css('display',
								'none');
							$('#hiddenContainer3').css('display',
								'none');
							$('#hiddenContainer4').css('display',
								'none');
							$('#hiddenContainer5').css('display',
								'none');
							$('#typeContainer').css('display', 'block');
							vm.yearSel = false;
						}
						$('#dateOnNotice').datepicker({
							format: 'dd/mm/yyyy'
						});

						$('#dateOnWhichNoticeReceived').datepicker({
							format: 'dd/mm/yyyy'
						});

						$('#dateOnWhichNoticeRequiredToReply')
							.datepicker({
								format: 'dd/mm/yyyy'
							});

						$('#dateOfReply').datepicker({
							format: 'dd/mm/yyyy'
						});

						$(
							'#dateOnNotice, #dateOnWhichNoticeReceived, #dateOnWhichNoticeRequiredToReply, #dateOfReply')
							.on('changeDate', function(ev) {
								$(this).datepicker('hide');
							});
					});
			// ViewNoticeService.fetchNoticeInfo(item.noticeInfo)
			// .then(function(result) {
			// vm.noticeInfo = result.data.noticesMaster;
			//	    			
			//			
			// }, function caseError(error){
			// alert('There seems some problem. Please try again later...');
			// return false;
			// });
			var modalInstance = $uibModal.open({
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'editNotice.html',
				size: 'xl',
				scope: $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function search(form) {

			var comp = '';
			var busi = '';
			var vert = '';
			var caseTy = '';
			var noticeStatus = '';
			var gstStatus = '';
			var gstSection = '';
			var state = '';
			var ty = '';

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

			if (vm.gstStatusInfo.length == $('#gstStatus').val().length)
				gstStatus = '0';
			else
				gstStatus = $('#gstStatus').val().toString();

			if (vm.gstTypeInfo.length == $('#gstType').val().length)
				ty = '0';
			else
				ty = $('#gstType').val().toString();

			if (vm.gstSectionInfo.length == $('#gstSection').val().length)
				gstSection = '0';
			else
				gstSection = $('#gstSection').val().toString();

			if (vm.stateInfo.length == $('#state').val().length)
				state = '0';
			else
				state = $('#state').val().toString();

			var dateOnWhichNoticeRequiredToReply = vm.form.dateOnWhichNoticeRequiredToReply;

			if ((comp == "") && ((busi == "0") || (busi == ""))
				&& ((vert == "0") || (vert == "")) && (caseTy == "")
				&& (noticeStatus == "") && (state == "") && (stDate == "")
				&& (endDate == "")
				&& (dateOnWhichNoticeRequiredToReply == '')) {
				alert("Please select atleast one filter...");
				return false;
			}
			var jsonReq = {
				"company": comp.toString(),
				"business": busi.toString(),
				"vertical": vert.toString(),
				"gstSection": gstSection,
				"gstType": ty,
				"gstStatus": gstStatus,
				"userID": localStorageService.get('payrollNo'),
				"noticeStatus": noticeStatus,
				"state": state,
				"dateRequiredToReply": dateOnWhichNoticeRequiredToReply,
				"sessID": localStorageService.get('sessionID')
			}
			localStorageService.set('searchNoticeDetails', jsonReq);
			GstService.testApi(jsonReq).then(function(result) {

				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}

				var gstMasterArray = result.data.gstMaster || [];
				var hasMatch = gstMasterArray.some(function(item) {
					return item.status === "Matched";
				});

				if (hasMatch) {
					console.log("Found a matched record!");
					vm.noticesInfo = result.data.gstMaster;
					$("#searchTableContainer").css("display", "block");
				}
			}

				, function caseError(error) {
					alert('There seems some problem. Please try again later...');
					return false;
				});

		}

		function fetchAllMaster() {
			GstService
				.fetchMaster()
				.then(
					function(result) {
						if (result.data.status == 'S') {
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
						vm.companyInfo = result.data.companyArray;
						vm.caseTypeInfo = result.data.caseTypeArray;
						vm.gstTypeInfo = result.data.gstTypeArray;
						vm.gstSectionInfo = result.data.gstSectionArray;
						vm.gstStatusInfo = result.data.gstStatusArray;
						vm.noticeTypeInfo = result.data.noticeTypeArray;
						vm.noticeStatusInfo = result.data.noticeStatusArray
						vm.stateInfo = result.data.stateArray;
						vm.priorityInfo = result.data.priorityArray;
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
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 2,
												nonSelectedText: 'Select Company',
												selectAllValue: 0,
												selectAllName: 'company-select',
												onChange: function(
													element,
													checked) {

													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
													var selComp = $(
														'#company')
														.val();
													if (selComp.length == 0)
														return false;
													vm.verticalInfo = [];
													$(
														"#vertical")
														.empty();
													$(
														"#vertical")
														.multiselect(
															"rebuild");

													vm.businessInfo = [];
													// $("#business").val([]);
													$(
														'#business')
														.empty();
													$(
														"#business")
														.multiselect(
															"refresh");

													var jsonReq = {
														"type": "business",
														"id": selComp
															.toString(),
														"userID": localStorageService
															.get('payrollNo'),
														"sessID": localStorageService
															.get('sessionID')
													}
													AddUserService
														.fetchBusiVeriView(
															jsonReq)
														.then(
															function(
																result) {
																if (result.data.status == 'F') {
																	alert(result.data.msg);
																	return false;
																}
																vm.businessInfo = result.data.resultArray;
																for (var i = 0; i < vm.businessInfo.length; i++) {
																	$(
																		'#business')
																		.append(
																			'<option value="'
																			+ vm.businessInfo[i].id
																			+ '">'
																			+ vm.businessInfo[i].name
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
												onSelectAll: function() {
													vm.verticalInfo = [];
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
													var selComp = $(
														'#company')
														.val();
													if (selComp.length == 0) {
														vm.verticalInfo = [];
														$(
															'#vertical')
															.empty();
														$(
															"#vertical")
															.multiselect(
																"rebuild");
														vm.businessInfo = [];
														$(
															'#business')
															.empty();
														$(
															"#business")
															.multiselect(
																"refresh");
														return false;

													}
													vm.verticalInfo = [];
													$(
														'#vertical')
														.empty();
													$(
														"#vertical")
														.multiselect(
															"rebuild");
													vm.businessInfo = [];
													$(
														'#business')
														.empty();
													$(
														"#business")
														.multiselect(
															"refresh");

													var jsonReq = {
														"type": "business",
														"id": selComp
															.toString(),
														"userID": localStorageService
															.get('payrollNo'),
														"sessID": localStorageService
															.get('sessionID')
													}
													AddUserService
														.fetchBusiVeriView(
															jsonReq)
														.then(
															function(
																result) {
																if (result.data.status == 'F') {
																	alert(result.data.msg);
																	return false;
																}
																vm.businessInfo = result.data.resultArray;
																for (var i = 0; i < vm.businessInfo.length; i++) {
																	$(
																		'#business')
																		.append(
																			'<option value="'
																			+ vm.businessInfo[i].id
																			+ '">'
																			+ vm.businessInfo[i].name
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
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 1,
												nonSelectedText: 'Select Business',
												selectAllValue: 0,
												selectAllName: 'business-select',
												onChange: function(
													element,
													checked) {
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
													var selComp = $(
														'#business')
														.val();
													if (selComp.length == 0)
														return false;
													vm.verticalInfo = [];
													$(
														'#vertical')
														.empty();
													$(
														"#vertical")
														.multiselect(
															"refresh");
													var jsonReq = {
														"type": "vertical",
														"id": selComp
															.toString(),
														"userID": localStorageService
															.get('payrollNo'),
														"sessID": localStorageService
															.get('sessionID')
													}
													AddUserService
														.fetchBusiVeriView(
															jsonReq)
														.then(
															function(
																result) {
																if (result.data.status == 'F') {
																	alert(result.data.msg);
																	return false;
																}
																vm.verticalInfo = result.data.resultArray;
																for (var i = 0; i < vm.verticalInfo.length; i++) {
																	$(
																		'#vertical')
																		.append(
																			'<option value="'
																			+ vm.verticalInfo[i].id
																			+ '">'
																			+ vm.verticalInfo[i].fullname
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
												onSelectAll: function() {
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
																"refresh");
														return false;
													}

													$(
														'#vertical')
														.empty();
													$(
														"#vertical")
														.multiselect(
															"refresh");
													var jsonReq = {
														"type": "vertical",
														"id": selComp
															.toString(),
														"userID": localStorageService
															.get('payrollNo'),
														"sessID": localStorageService
															.get('sessionID')
													}
													AddUserService
														.fetchBusiVeriView(
															jsonReq)
														.then(
															function(
																result) {
																if (result.data.status == 'F') {
																	alert(result.data.msg);
																	return false;
																}
																vm.verticalInfo = result.data.resultArray;
																for (var i = 0; i < vm.verticalInfo.length; i++) {
																	$(
																		'#vertical')
																		.append(
																			'<option value="'
																			+ vm.verticalInfo[i].id
																			+ '">'
																			+ vm.verticalInfo[i].fullname
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
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 1,
												nonSelectedText: 'Select Vertical',
												selectAllValue: 0,
												selectAllName: 'vertical-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
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
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 2,
												nonSelectedText: 'Select Case Category',
												selectAllValue: 0,
												selectAllName: 'caseType-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
												}

											});

									$('#gstType')
										.multiselect(
											{
												templates: {
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 2,
												nonSelectedText: 'Select Type',
												selectAllValue: 0,
												selectAllName: 'type-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
												}

											});

									$('#gstSection')
										.multiselect(
											{
												templates: {
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 2,
												nonSelectedText: 'Select Section',
												selectAllValue: 0,
												selectAllName: 'type-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
												}

											});

									$('#gstStatus')
										.multiselect(
											{
												templates: {
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 2,
												nonSelectedText: 'Select Status',
												selectAllValue: 0,
												selectAllName: 'type-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
												}

											});


									$('.dropdown-menu')
										.click(
											function(
												event) {
												event
													.stopPropagation();
											});
									$('#noticeStatus')
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
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 1,
												nonSelectedText: 'Select Notice Status',
												selectAllValue: 100000000,
												selectAllName: 'forum-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
													$(
														'ul.dropdown-menu')
														.removeClass(
															'show');
												}

											});

									$('#state')
										.multiselect(
											{
												templates: {
													// li:
													// '<li><a
													// class="dropdown-item"><label
													// class="m-0
													// pl-2
													// pr-0"></label></a></li>',
													ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
													button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
													filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
													filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
												},
												buttonContainer: '<div class="dropdown" />',
												buttonClass: 'btn btn-white',
												enableFiltering: true,
												enableCaseInsensitiveFiltering: true,
												maxHeight: 250,
												includeSelectAllOption: true,
												numberDisplayed: 1,
												nonSelectedText: 'Select State',
												selectAllValue: 0,
												selectAllName: 'caseType-select',
												onChange: function(
													element,
													checked) {
													// $('ul.dropdown-menu').removeClass('show');
												},
												onSelectAll: function() {
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

		function fetchCompanyMaster(item) {
			vm.selComp = item;
			dashboardService.fetchCompanyMaster(item).then(function(result) {
				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}
				vm.businessInfo = result.data.businessMaster;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function fetchVerticalMaster(item) {
			vm.selBusiness = item;
			dashboardService.fetchVerticalMaster(item).then(function(result) {
				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}
				vm.verticalInfo = result.data.verticalMaster;
			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function fetchNoticeSection(item) {
			vm.selNoticyType = item;
			AddNoticeService.fetchNoticeSection(item).then(function(result) {
				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}
				vm.sectionInfo = result.data.noticeSectionMaster;
			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function fetchSectionDesc(section) {
			var temp = _.findWhere(vm.sectionInfo, {
				id: section
			});
			vm.form.sectionDesc = temp.desc;
		}
		
		function viewAdditionalNoticeItem(item){ 							
			GstService.viewCase(vm.noticesInfo[item].refId,vm.noticesInfo[item].type)
				    		.then(function(result) {    			
				    			if(result.data.status == 'F'){
									alert(result.data.msg);
									return false;
								}
				    			 vm.caseDetails = result.data;
				    			 localStorageService.set('viewCaseDetails' , vm.caseDetails )
				    			 $state.goToNewTab('viewadditionalnotice', {});

						 }, function caseError(error){
						      alert('There seems some problem. Please try again later...');
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
					"company": form.company.$modelValue,
					"business": form.business.$modelValue,
					"vertical": parseInt(form.vertical.$modelValue),
					"caseType": parseInt(form.caseType.$modelValue),
					"type": 'edit',
					"asstYear": form.assessYear.$modelValue,
					"panNo": form.panNo.$modelValue,
					"noticeType": form.noticeType.$modelValue,
					"noticeSection": form.section.$modelValue,
					"issuedByName": form.issuedByName.$modelValue,
					"issuedByDesig": form.issuedByDesig.$modelValue,
					"issuedByOrganization": form.issuedByOrganization.$modelValue,
					"dateOnNotice": form.dateOnNotice.$modelValue,
					"dateOnWhichNoticeReceived": form.dateOnWhichNoticeReceived.$modelValue,
					"dateOnWhichNoticeRequiredToReply": form.dateOnWhichNoticeRequiredToReply.$modelValue,
					"dateOfReply": form.dateOfReply.$modelValue,
					"remarks": form.remarks.$modelValue,
					"status": form.status.$modelValue,
					"enteredBy": localStorageService.get('payrollNo'),
					"sessID": localStorageService.get('sessionID'),
					"id": parseInt(vm.noticeID),
					"addressedTo": form.addressedTo.$modelValue,
					"natureOfParty": form.natureOfParty.$modelValue,
					"briefFacts": form.briefFacts.$modelValue,
					"stakeInvolment": form.stakeInvolment.$modelValue,
					"priority": form.priority.$modelValue,
					"userID": localStorageService.get('payrollNo')

				}

				AddNoticeService
					.addNewNotice(jsonReq)
					.then(
						function(result) {

							if (result.data.status == 'T') {
								alert('Notice details edited successfully with ID - '
									+ result.data.id);
								window.location.reload();

							} else {
								alert(result.data.msg);
								return false;
							}
						},
						function loginError(error) {

							alert('Something went wrong... Pls try again...');
							return false;
						});

			} else
				return;

		}

	}

})();
