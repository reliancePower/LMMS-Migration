(function() {
	'use strict';

	angular.module('regCalApp.dashboard').controller('DashboardController',
			DashboardController);

	DashboardController.$inject = [ '$state', 'dashboardService',
			'DTOptionsBuilder', 'DTColumnBuilder', '$scope',
			'localStorageService', '$compile', '$uibModal', '$uibModalStack',
			'SearchCaseService', '$window', 'AddUserService', 'DTColumnDefBuilder', 'navbarService' ];

	/* @ngInject */
	function DashboardController($state, dashboardService, DTOptionsBuilder,
			DTColumnBuilder, $scope, localStorageService, $compile, $uibModal,
			$uibModalStack, SearchCaseService, $window, AddUserService, DTColumnDefBuilder, navbarService) {
		var vm = angular.extend(this, {

			lastLogin : '',
			dtOptions1 : {},
			dtOptions2 : {},
			today : new Date(),
			todayHearingArray : [],
			upComingHearingArray : [],
			newAlertsArray : [],
			viweCaseItem : viweCaseItem,
			viweAlertItem : viweAlertItem,
			title : '',
			finImpTable : '',
			caseDetails : '',
			fetchUpcoming : fetchUpcoming,
			printTable : printTable,
			stateInfo : [],
			counter : 0,		
			notificationFlag : 'F',
			changeAction : changeAction,
			action : 0,
			submit : submit,
			existingCaseID : '',
			remark : '',
			selAlert : [],
			alertActionInfo : [],
			showContainer : true,
			fetchAllMaster : fetchAllMaster,
			fetchCompanyMaster : fetchCompanyMaster,
			fetchVerticalMaster : fetchVerticalMaster,
			companyInfo : [],
			businessInfo : [],
			verticalInfo : [],
			caseTypeInfo : [],
			form : {
				company : 99,
				startDate : '',
				endDate : '',
				notificationType : 99
			},
			searchAlert : searchAlert,
			fetchAlertMaster : fetchAlertMaster,
			compArrayInfo : [],
			notificationTypeInfo : [],
			hearing : [],
			finImpArray : [],
			currency : '',
			grandTotal : 0,
			dtColumnDefs : []

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}

			vm.userType = localStorageService.get('userType');

			var days = 7; // Days you want to subtract
			var date = new Date();
			var first = new Date(date.getTime() + (1 * 24 * 60 * 60 * 1000));
			var last = new Date(date.getTime() + (days * 24 * 60 * 60 * 1000));
			var day1 = ("0" + last.getDate()).slice(-2);
			var month1 = ("0" + (last.getMonth() + 1)).slice(-2);
			var days = (day1) + '/' + (month1) + '/' + last.getFullYear()
			var day2 = ("0" + first.getDate()).slice(-2);
			var month2 = ("0" + (first.getMonth() + 1)).slice(-2);
			var days2 = (day2) + '/' + (month2) + '/' + first.getFullYear()
			vm.endDate = days;
			var day3 = ("0" + date.getDate()).slice(-2);
			var month3 = ("0" + (date.getMonth() + 1)).slice(-2);
			var days3 = (day3) + '/' + (month3) + '/' + date.getFullYear();
			vm.form.startDate = days3;
			vm.form.endDate = days3;
			vm.todayAs = days3;
			vm.startDate = days2;
			vm.state = 0;
			
			getHomePageInfo();
			fetchAlertMaster();
			localStorageService.remove('searchCaseDetails');
			vm.dtOptions2 = DTOptionsBuilder
					.newOptions()
					.withPaginationType('full_numbers')
					.withOption('order', [])
				 .withOption('autoWidth', false)
		              .withOption('deferRender', true)
					.withOption(
							'rowCallback',
							function rowCallback(nRow, aData, iDisplayIndex,
									iDisplayIndexFull) {

								$('td', nRow).unbind('click');
								$('td', nRow).bind('click', function() {
									$scope.$apply(function() {
										vm.viweCaseItem(aData, iDisplayIndex);
									});
								});
								return nRow;
							})
					.withButtons(
							[
									{
										extend : "excelHtml5",
										class : 'btn btn-info',
										fileName : "Upcoming Cases",
										title : "List of Cases scheduled for hearing between "
												+ vm.startDate
												+ ' and '
												+ vm.endDate
												+ ((vm.state == '' || vm.state == undefined) ? ''
														: ' for the Location '
																+ vm.state),
																autoFilter : true,
														        exportOptions: {			        	
														        	format : {
															        	body: function ( data, row, column, node ) {
															        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
															        			var cont = node.textContent.substring(0, node.textContent.length -8);
															        			
															        			return  cont;
															        		}else if (node.id == 'caseID'){
															        			return node.textContent.trim();
																        		}
															        		else
															                return  data;
															            }
															        },
											                    
														            columns: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41]
														        },
														        text : 'Excel Master Data',
														        
														        exportData: { decodeEntities: true }
										
									},{
										extend: "colvis",			      
								        columns: ':not(.noVis)',
								       text:"Show/Hide Columns"
								        
								    
								 },{
										extend : "excelHtml5",
										class : 'btn btn-info',
										fileName : "Upcoming Cases",
										title : "List of Cases scheduled for hearing between "
												+ vm.startDate
												+ ' and '
												+ vm.endDate
												+ ((vm.state == '' || vm.state == undefined) ? ''
														: ' for the Location '
																+ vm.state),
																autoFilter : true,
														        exportOptions: {			        	
														        	format : {
															        	body: function ( data, row, column, node ) {
															        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
															        			var cont = node.textContent.substring(0, node.textContent.length -8);
															        			
															        			return  cont;
															        		}else if (node.id == 'caseID'){
															        			return node.textContent.trim();
																        		}
															        		else
															                return  data;
															            }
															        },
											                    
															        columns: ':visible'
														        },
														        
														        
														        exportData: { decodeEntities: true }
										
									},
									{
										extend : "pdfHtml5",
										 orientation : 'landscape',
								          pageSize : 'A2',
										fileName : "Upcoming Cases",
										title : "List of Cases scheduled for hearing between "
												+ vm.startDate
												+ ' and '
												+ vm.endDate
												+ ((vm.state == '' || vm.state == undefined) ? ''
														: ' for the  '
																+ vm.state + ' Location'),
																 exportOptions: {

														        	 format : {
														        	body: function ( data, row, column, node ) {
														        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
														        			var cont = node.textContent.substring(0, node.textContent.length -8);
														        			
														        			return  cont;
														        		}else if (node.id == 'caseID'){
														        			return node.textContent.trim();
															        		}
														        		else
														                return  data;
														            }
														        },
														             columns: ':visible'
														        },
														        header: true,													       
														      
														        customize: function(doc, ev) {
														        	var cols = doc.content[1].table.body[0].length;
														        	if(cols > 15){
														        		alert('Only 15 columns are allowed to make PDF.');
														        		
														        		ev.stopPropagation();
														        		return false;
														        	}
														        
														        } ,
														        exportData: {decodeEntities:true}

									},
									{
										extend : "print",
										 exportOptions: {

								        	 format : {
								        	body: function ( data, row, column, node ) {
								        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
								        			var cont = node.textContent.substring(0, node.textContent.length -8);
								        			
								        			return  cont;
								        		}
								        		else
								                return  data;
								            }
								        },
								             columns: ':visible'
								        },
										autoPrint : true,
										title : "List of Cases scheduled for hearing between "
												+ vm.startDate
												+ ' and '
												+ vm.endDate
												+ ((vm.state == '' || vm.state == undefined) ? ''
														: ' for the State/Location '
																+ vm.state),
																  customize: function(doc, ev) {
																		$.fn.columnCount = function() {
																			    return $('th', $(this).find('thead')).length;
																			};
																			
																			var colctr = $('#upcomingHearing').columnCount();
																			
																			
																        	if(colctr > 15){
																        		alert('Only 15 columns are allowed to Print.');
																        		doc.close();
																        		ev.stopPropagation();
																        		return false;
																        	}else {
																        		var last = null;
																                var current = null;
																                var bod = [];
																 
																                var css = '@page { size: landscape; }',
																                    head = doc.document.head || doc.document.getElementsByTagName('head')[0],
																                    style = doc.document.createElement('style');
																 
																                style.type = 'text/css';
																                style.media = 'print';
																 
																                if (style.styleSheet)
																                {
																                  style.styleSheet.cssText = css;
																                }
																                else
																                {
																                  style.appendChild(doc.document.createTextNode(css));
																                }
																 
																                head.appendChild(style);
																        	}
																        
																        } 
									}
									

							]);

			vm.dtOptions3 = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', [])
// .withOption('autoWidth', false)
		              .withOption('deferRender', true)
			.withOption(
					'rowCallback',
					function rowCallback(nRow, aData, iDisplayIndex,
							iDisplayIndexFull) {
						$('td', nRow).unbind('click');
						$('td', nRow).bind('click', function() {
							$scope.$apply(function() {
								vm.viweAlertItem(aData, iDisplayIndex);
							});
						});
						return nRow;
					})

			.withButtons([ {
				extend : "excelHtml5",
				fileName : "New Alerts",
				title : "New Alerts",
				exportOptions : {
					columns : ':visible'
				},

				exportData : {
					decodeEntities : true
				}
			}, {
				extend : "pdfHtml5",
				pageSize : 'A2',
				 orientation : 'landscape',
				fileName : "New Alerts",
				title : "New Alerts",
				exportOptions : {
					columns : ':visible'
				},
				exportData : {
					decodeEntities : true
				}

			}, {
				extend : "print",
				exportOptions : {
					columns : ':visible'
				},
				autoPrint : true,
				title : "New Alerts"

			}

			]);
			
			vm.dtColumnDefs = [
	   			 DTColumnDefBuilder.newColumnDef(1).notVisible(),
	      		 DTColumnDefBuilder.newColumnDef(2).notVisible(),
	      		DTColumnDefBuilder.newColumnDef(5).notVisible(),
	      	     DTColumnDefBuilder.newColumnDef(7).notVisible(),
	      	     DTColumnDefBuilder.newColumnDef(11).notVisible(),
	      	     DTColumnDefBuilder.newColumnDef(12).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(14).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(15).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(16).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(19).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(20).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(21).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(23).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(24).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(25).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(26).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(27).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(28).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(29).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(30).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(31).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(32).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(33).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(34).notVisible(),
	   		   	 DTColumnDefBuilder.newColumnDef(35).notVisible(),
		   		 DTColumnDefBuilder.newColumnDef(36).notVisible(),
		   		 DTColumnDefBuilder.newColumnDef(37).notVisible(),
		   		 DTColumnDefBuilder.newColumnDef(38).notVisible(),
		   		 DTColumnDefBuilder.newColumnDef(39).notVisible(),
		   		 DTColumnDefBuilder.newColumnDef(40).notVisible(),
		   		 DTColumnDefBuilder.newColumnDef(41).notVisible(),
	   		   
	   		  DTColumnDefBuilder.newColumnDef([16,17]).withOption('type', 'date-euro')
	      	       ];

		})();

	

		function fetchAlertMaster() {
			var jsonReq = {
				"type" : "fetchMaster",
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}

			dashboardService
					.manageMaster(jsonReq)
					.then(
							function(result) {
//								if(result.data.status == 'F'){
//									alert(result.data.msg);
//									return false;
//								}
								vm.compArrayInfo = result.data.compArray;
								vm.notificationTypeInfo = result.data.notificationTypeArray;
								vm.notificationTypeInfo.push({
									'id' : 0,
									'name' : 'all',
									'desc' : 'All'
								});
								vm.notificationTypeInfo = _.sortBy(
										vm.notificationTypeInfo, 'id');
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});
		}
		function searchAlert() {

			var jsonReq = {
				"type" : "searchAlert",
				"company" : vm.form.company,
				"startDate" : vm.form.startDate,
				"notificationType" : vm.form.notificationType,
				"endDate" : vm.form.endDate,
				"enteredBy" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				"userID" : localStorageService.get('payrollNo')
			}
			
			dashboardService.searchAlert(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.newAlertsArray = result.data.newAlertsArray;
				$('#alertsTable').css('display', 'block');
			
			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		
		function fetchUpcoming() {
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"endDate" : vm.endDate,
				"startDate" : vm.startDate,
				"state" : vm.state.toString(),
				"sessID" : localStorageService.get('sessionID')
			}
			dashboardService.getSummaryUpcoming(data).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.upComingHearingArray = result.data.upComingHearingArray;
				$('#upcomingTable').css('display', 'block');

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function getHomePageInfo() {
			dashboardService.getHomePageInfo().then(function(result) {
			
					if(result.data.status == 'S'){
						alert(result.data.msg);
						
							$window.localStorage.clear();
							localStorageService.clearAll;
							localStorageService.cookie.clearAll;
							$state.go('login');
						} 
				
				vm.lastLogin = result.data.lastLoginTime;
				localStorageService.set('lastLogin', vm.lastLogin);
				vm.todayHearingArray = result.data.todayHearingArray;
				vm.stateInfo = result.data.stateArray;
				vm.stateInfo.push({
					"id" : 0,
					"name" : "All"
				});

				vm.alertActionInfo = result.data.alertActionArray;
				vm.notificationFlag = result.data.notificationStatus;
				localStorageService.set('userType', result.data.userType);
				tableActivity1();

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function viweAlertItem(aData, index) {
			var id = parseInt(aData[0]);
			vm.action = 0;
			vm.existingCaseID = '';
			vm.showContainer = true;
			vm.remark = '';
			vm.selAlert = _.findWhere(vm.newAlertsArray, {
				'id' : id
			});
			vm.alertID = vm.selAlert.id;
			vm.company = vm.selAlert.company;
			vm.business = vm.selAlert.business;
			vm.vertical = vm.selAlert.vertical;
			vm.caseType = vm.selAlert.caseType;
			vm.forum = vm.selAlert.forum;
			vm.forumName = vm.selAlert.forumName;
			vm.caseNo = vm.selAlert.caseNo;
			vm.petitioner = vm.selAlert.petitioner;
			vm.respondent = vm.selAlert.respondent;
			vm.bench = vm.selAlert.bench;
			vm.counselOfCompany = vm.selAlert.counselOfCompany;
			vm.counselOfRespondent = vm.selAlert.counselOfRespondent;
			vm.nextHearingDate = vm.selAlert.nextHearingDate;
			vm.subMatter = vm.selAlert.subMatter;
			vm.caseStatus = vm.selAlert.caseStatus;
			vm.caseCategory = vm.selAlert.caseCategory;
			vm.caseTypeLMMS = 0;
			vm.companyName = vm.selAlert.companyName;
			vm.businessName = vm.selAlert.businessName;
			vm.verticalName = vm.selAlert.verticalName;
			vm.notificationType = vm.selAlert.notificationType;
			vm.dairyNo = vm.selAlert.dairyNo;
			if (vm.notificationType != 'proactive_alert')
				vm.existingCaseID = vm.selAlert.lmmsID;
			else
				vm.existingCaseID = '';
			vm.keywords = vm.selAlert.keywords;
			vm.companyInfo.push({
				'id' : vm.selAlert.company,
				'name' : vm.selAlert.companyName
			});
			vm.companyInfo.push({
				'id' : vm.selAlert.company,
				'name' : vm.selAlert.companyName
			});
			vm.businessInfo.push({
				'id' : vm.selAlert.business,
				'name' : vm.selAlert.businessName
			});
			vm.verticalInfo.push({
				'id' : vm.selAlert.vertical,
				'name' : vm.selAlert.verticalName
			});

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'viewAlert.html',
				size : 'xl',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
				$("[data-toggle=popover]").popover('hide');
			});
		}

		function viweCaseItem(aData, index) {

			var id = parseInt(aData[0])
			SearchCaseService.viewCase(id).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.caseDetails = result.data;
				localStorageService.set('viewCaseDetails', vm.caseDetails)
				$state.goToNewTab('viewcase', {});

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function fetchAllMaster() {
			AddUserService.fetchAllMasterUser().then(function(result) {
				if(result.data.status == 'F'){
					//alert(result.data.msg);
					return false;
				}
				vm.companyInfo = result.data.companyArray;
				vm.businessInfo = result.data.businessArray;
				var temp = result.data.verticalArray;
				vm.caseTypeInfo = result.data.caseTypeArray;
				var keyVal = {
					'fullname' : 'name'
				};
				_.each(temp, function(value, key) {
					key = keyVal[key] || key;
					vm.verticalInfo[key] = value;
				});

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function fetchCompanyMaster(item) {

			dashboardService.fetchCompanyMaster(item).then(function(result) {
				if(result.data.status == 'F'){
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
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.verticalInfo = result.data.verticalMaster;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function printTable() {

			var title = '<img  style = "width: 200px;height: auto;padding: 16px 15px 0px 10px;" src = "app/img/logo.png" /><h5 style = "display: inline-block;padding-left: 50px;color : #005199 !important;font-weight:600;">'
					+ $('#textTitle').text() + '</h5><hr>';
			var w = window.open();
			w.document.write($("#print-header").html() + title
					+ $("#viewAllMatter").html() + $("#print-footer").html());
			w.document.close();
			w.focus();
		}

		

		function tableActivity1() {

			vm.dtOptions1 = DTOptionsBuilder
					.newOptions()
					.withPaginationType('full_numbers')
					.withOption('order', [])
				 .withOption('autoWidth', false)
		              .withOption('deferRender', true)
					.withOption(
							'rowCallback',
							function rowCallback(nRow, aData, iDisplayIndex,
									iDisplayIndexFull) {
								// Unbind first in order to avoid any duplicate
								// handler (see
								// https://github.com/l-lin/angular-datatables/issues/87)
								$('td', nRow).unbind('click');
								$('td', nRow).bind('click', function() {
									$scope.$apply(function() {
										vm.viweCaseItem(aData, iDisplayIndex);
									});
								});
								return nRow;
							})
					.withButtons(
							[
									{
										extend : "excelHtml5",
										fileName : "List of Cases scheduled for hearing on  "
												+ vm.todayAs,
										title : "List of Cases scheduled for hearing on  "
												+ vm.todayAs,
												 autoFilter : true,
											        exportOptions: {			        	
											        	format : {
												        	body: function ( data, row, column, node ) {
												        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
												        			var cont = node.textContent.substring(0, node.textContent.length -8);
												        			
												        			return  cont;
												        		}else if (node.id == 'caseID'){
												        			return node.textContent.trim();
													        		}
												        		else
												                return  data;
												            }
												        },
								                    
											            columns: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41]
											        },
											        text : 'Excel Master Data',
											        
											        exportData: { decodeEntities: true }
									},
									{
								        extend: "colvis",			      
								        columns: ':not(.noVis)',
								       text:"Show/Hide Columns"
								        
								    
								 },{
										extend : "excelHtml5",
										fileName : "List of Cases scheduled for hearing on  "
												+ vm.todayAs,
										title : "List of Cases scheduled for hearing on  "
												+ vm.todayAs,
												 autoFilter : true,
											        exportOptions: {			        	
											        	format : {
												        	body: function ( data, row, column, node ) {
												        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
												        			var cont = node.textContent.substring(0, node.textContent.length -8);
												        			
												        			return  cont;
												        		}else if (node.id == 'caseID'){
												        			return node.textContent.trim();
													        		}
												        		else
												                return  data;
												            }
												        },
								                    
												        columns: ':visible'
												        	},
											        
											        
											        exportData: { decodeEntities: true }
									},
									{
										extend : "pdfHtml5",
										 orientation : 'landscape',
								            pageSize : 'A3',
										fileName : "List of Cases scheduled for hearing on  "
												+ vm.todayAs,
										title : "List of Cases scheduled for hearing on  "
												+ vm.todayAs,
												 exportOptions: {

										        	 format : {
										        	body: function ( data, row, column, node ) {
										        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
										        			var cont = node.textContent.substring(0, node.textContent.length -8);
										        			
										        			return  cont;
										        		}else if (node.id == 'caseID'){
										        			return node.textContent.trim();
											        		}
										        		else
										                return  data;
										            }
										        },
										             columns: ':visible'
										        },
										        header: true,													       
										      
										        customize: function(doc, ev) {
										        	var cols = doc.content[1].table.body[0].length;
										        	if(cols > 15){
										        		alert('Only 15 columns are allowed to make PDF.');
										        		
										        		ev.stopPropagation();
										        		return false;
										        	}
										        
										        } ,
										        exportData: {decodeEntities:true}
									},
									{
										extend : "print",
										 exportOptions: {

								        	 format : {
								        	body: function ( data, row, column, node ) {
								        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
								        			var cont = node.textContent.substring(0, node.textContent.length -8);
								        			
								        			return  cont;
								        		}
								        		else
								                return  data;
								            }
								        },
								             columns: ':visible'
								        },
										autoPrint : true,
										title : "List of Cases scheduled for hearing on  " + vm.todayAs,
												   customize: function(doc, ev) {
													  
														$.fn.columnCount = function() {
															    return $('th', $(this).find('thead')).length;
															};
															
															var colctr = $('#todaysHearing').columnCount();
															
															
												        	if(colctr > 15){
												        		alert('Only 15 columns are allowed to Print.');
												        		doc.close();
												        		ev.stopPropagation();
												        		return false;
												        	}else {
												        		var last = null;
												                var current = null;
												                var bod = [];
												 
												                var css = '@page { size: landscape; }',
												                    head = doc.document.head || doc.document.getElementsByTagName('head')[0],
												                    style = doc.document.createElement('style');
												 
												                style.type = 'text/css';
												                style.media = 'print';
												 
												                if (style.styleSheet)
												                {
												                  style.styleSheet.cssText = css;
												                }
												                else
												                {
												                  style.appendChild(doc.document.createTextNode(css));
												                }
												 
												                head.appendChild(style);
												        	}
												        
												        } 
												

									}

							]);

			

		}

		function changeAction(item) {
			if (item == 4) {
				vm.fetchAllMaster();
				vm.showContainer = false;
			}
		}

		function submit(item, id, type) {

			if (item == 2 || item == 3 || type != 'proactive_alert') {
				if (vm.existingCaseID = '' || vm.remark == '') {
					alert("Please enter all details");
					return false;
				}
				var jsonReq = {
					"type" : "updateAlert",
					"alertID" : parseInt(id),
					"remark" : vm.remark,
					"action" : vm.action,
					"enteredBy" : localStorageService.get('payrollNo'),
					"lmmsID" : (vm.existingCaseID == '') ? 0
							: vm.existingCaseID,
							"userID" : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
				}
				// alert(JSON.stringify(jsonReq))
				dashboardService.manageMaster(jsonReq).then(function(result) {

					if (result.data.status == 'T') {
						alert('Alert details updated successfully');
						$uibModalStack.dismissAll();
						searchAlert();
						// $window.location.reload();

					} else {
						alert(result.data.msg);
						return false;
					}
				}, function loginError(error) {

					alert('Something went wrong... Pls try again...');
					return false;
				});
			} else if (item == 1) {
				localStorageService.set('alert', item);
				localStorageService.set('alertItem', vm.selAlert);
				$state.go('addcase');
				// alert("");
			} else if (item == 4) {

				if (vm.company == undefined || vm.business == undefined
						|| vm.vertical == undefined
						|| vm.caseTypeLMMS == undefined || vm.company == 0
						|| vm.business == 0 || vm.vertical == 0
						|| vm.caseTypeLMMS == 0) {
					alert("Please select all the fields");
					return false;
				}

				var jsonReq = {
					"type" : "editAlert",
					"alertID" : parseInt(id),
					"remark" : vm.remark,
					"action" : vm.action,
					"enteredBy" : localStorageService.get('payrollNo'),
					"lmmsID" : (vm.existingCaseID == '') ? 0
							: vm.existingCaseID,
					'companyID' : vm.company,
					'businessID' : vm.business,
					'verticalID' : vm.vertical,
					'caseCategoryID' : vm.caseTypeLMMS,
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				// alert(JSON.stringify(jsonReq))
				dashboardService.manageMaster(jsonReq).then(
						function(result) {

							if (result.data.status == 'T') {
								alert('Alert details with ID ' + id
										+ ' edited successfully');
								$uibModalStack.dismissAll();
								searchAlert();
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
	}
})();


