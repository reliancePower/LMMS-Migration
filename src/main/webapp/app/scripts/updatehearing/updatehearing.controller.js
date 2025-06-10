(function() {
	'use strict';

	angular.module('regCalApp.updatehearing').controller(
			'UpdateHearingController', UpdateHearingController);

	UpdateHearingController.$inject = [ '$state', 'updatehearingService',
			'$scope', 'localStorageService', 'SearchCaseService', '$filter',
			'$window', '$uibModal', '$uibModalStack', 'DTOptionsBuilder',
			'DTColumnBuilder', 'DTColumnDefBuilder', 'addCaseService' ];

	/* @ngInject */
	function UpdateHearingController($state, updatehearingService, $scope,
			localStorageService, SearchCaseService, $filter, $window,
			$uibModal, $uibModalStack, DTOptionsBuilder, DTColumnBuilder,
			DTColumnDefBuilder, addCaseService) {
		var vm = angular.extend(this, {

			form : {
				lastDate : '',
				outcomeLast : '',
				nextDate : '',
				outcomeNext : '',
				bench : '',
				caseStatus : 0,
				counselOfCompany : '',
				counselOfRespondent : '',
				userID : ''
			},
			counselCompanyInfo : [],
			selCounselCompany : '',
			caseStatusInfo : [],
			openCounselCompModal : openCounselCompModal,
			openBenchModal : openBenchModal,
			counselCompanySelected : [],
			benchSelected : [],
			AddMember : AddMember,
			removeMember : removeMember,
			diplayMember : diplayMember,
			selBench : '',
			counselRespInfo : [],
			openCounselRespModal : openCounselRespModal,
			selCounselResp : '',
			counselRespSelected : [],
			aorRespInfo : [],
			benchModal : {},
			counselOfCompanyModal : {},
			counselOfRespModal : {},
			submit : submit,
			hearingHistory : [],
			caseID : '',
			editHearingItem : editHearingItem,
			cancelForm : cancelForm,
			AddNewHearing : AddNewHearing,
			outcomeLastModal : outcomeLastModal,
			outcomeNextModal : outcomeNextModal,
			addFlag : false,
			selitem : {},
			disableHearingItem : disableHearingItem,
			goBack : goBack,
			edited : false,
			caseItem : {}

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.caseItem = localStorageService.get('updatehearing');
			vm.userType = localStorageService.get('userType');
			vm.caseID = vm.caseItem.caseID;
			SearchCaseService.fetchMaster().then(function(result) {
				if (result.data.status == 'F') {
					alert(result.data.msg);
					return false;
				}
				vm.benchSelected = [];
				vm.counselCompanySelected = [];
				vm.counselRespSelected = [];
				vm.caseStatusInfo = result.data.statusArray;
				vm.counselCompanyInfo = result.data.counselArray;
				vm.counselRespInfo = result.data.counselArray;
				vm.benchInfo = result.data.benchArray;
			});
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID'),
				'caseID' : vm.caseID
			}

			updatehearingService.fetchHearingHistory(data).then(
					function(result) {
						if (result.data.status == 'F') {
							alert(result.data.msg);
							return false;
						}

						vm.hearingHistory = result.data.updateHearingArray;
					});

			vm.dtOptions3 = DTOptionsBuilder.newOptions().withOption('order',
					[]).withDisplayLength(200).withOption('autoWidth', false)
					.withOption('drawCallback', function() {
						$('.dataTables_paginate').hide();
					});

		})();

		function goBack() {
			$window.close();
			window.opener.location.reload();

		}

		function submit(form) {
			angular.forEach(form, function(obj) {
				if (angular.isObject(obj) && angular.isDefined(obj.$setDirty)) {
					obj.$setDirty();
				}
			})

			if (form.$valid) {
				var last = $filter('date')(vm.form.lastDateOfHearing,
						"dd/MM/yyyy");
				var next = $filter('date')(vm.form.nextDateOfHearing,
						"dd/MM/yyyy");

				if (!vm.addFlag
						&& vm.hearingHistory.length > 1
						&& (vm.form.outcomeLast == "" || last == "" || vm.form.bench == "")) {
					alert('Please enter the mandatory fields');
					return false;
				}

				var jsonReq = {
					"updateType" : (vm.addFlag == true) ? 'add' : 'edit',
					"bench" : vm.form.bench,
					"counselOfCompany" : form.counselOfCompany.$modelValue,
					"counselOfRespondent" : form.counselOfRespondent.$modelValue,
					"lastDateOfHearing" : (last == null) ? '' : last,
					"nextDateOfHearing" : (next == null) ? '' : next,
					"caseStatus" : vm.form.caseStatus,
					"outcomeLast" : vm.form.outcomeLast,
					"outcomeNext" : form.outcomeNext.$modelValue,
					"enteredBy" : localStorageService.get('userName'),
					"caseID" : vm.caseID,
					"userID" : localStorageService.get('payrollNo'),
					'type' : 'updateHearing',
					'id' : (vm.addFlag == true) ? 0 : vm.selitem.id,
					"sessID" : localStorageService.get('sessionID'),
					"lastHearingDate" : (last == null) ? undefined : last,
					"nextHearingDate" : (next == null) ? undefined : next

				}

				updatehearingService
						.updateHearing(jsonReq)
						.then(
								function(result) {

									if (result.data.status == 'T') {
										vm.edited = true;
										if (!vm.addFlag) {
											var latest = vm.hearingHistory[0];

											alert('Hearing details updated successfully');
											if (latest.id == vm.selitem.id)
												vm.caseItem = jsonReq;
										} else {
											alert('Hearing details for Case ID - '
													+ vm.caseItem.caseID
													+ ' added successfully');
											vm.caseItem = jsonReq;
										}
										$state.reload();

										$('#formContainer').css('display', 'none');
//										var data = {
//											"userID" : localStorageService
//													.get('payrollNo'),
//											"sessID" : localStorageService
//													.get('sessionID'),
//											'caseID' : vm.caseID
//										}
//
//										updatehearingService
//												.fetchHearingHistory(data)
//												.then(
//														function(result) {
//
//															vm.hearingHistory = result.data.updateHearingArray;
//														});
//										$('#formContainer').collapse('hide');

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

		function editHearingItem(item) {
			vm.addFlag = false;
			vm.edited = true;
			vm.selitem = vm.hearingHistory[item];
			vm.caseItem.caseID = vm.selitem.caseID;

			if (item == 0 || vm.selitem.nextHearingDate == undefined
					|| vm.selitem.nextHearingDate == '')
				$('#nextDateOfHearing').prop('disabled', false);
			else
				$('#nextDateOfHearing').prop('disabled', true);
			if (vm.selitem.lastHearingDate == undefined
					|| vm.selitem.lastHearingDate == '')
				$('#lastDateOfHearing').prop('disabled', false);
			else
				$('#lastDateOfHearing').prop('disabled', true);

			vm.form.caseStatus = vm.selitem.statusID;

			vm.form.bench = vm.selitem.bench;
			vm.form.counselOfCompany = vm.selitem.counselOfCompany;
			vm.form.counselOfRespondent = vm.selitem.counselOfRespondent;
			vm.form.outcomeLast = vm.selitem.outcomeLast;
			vm.form.outcomeNext = vm.selitem.outcomeNext;

			if (vm.selitem.bench != "" && vm.selitem.bench != undefined) {
				var temp = vm.selitem.bench.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.benchSelected.push(temp[i]);
			}

			if (vm.selitem.counselOfCompany != ""
					&& vm.selitem.counselOfCompany != undefined) {
				var temp = vm.selitem.counselOfCompany.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.counselCompanySelected.push(temp[i]);
			}

			if (vm.selitem.counselOfRespondent != ""
					&& vm.selitem.counselOfRespondent != undefined) {
				var temp = vm.selitem.counselOfRespondent.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.counselRespSelected.push(temp[i]);
			}
			vm.benchSelected = _.uniq(vm.benchSelected);
			vm.counselCompanySelected = _.uniq(vm.counselCompanySelected);
			vm.counselRespSelected = _.uniq(vm.counselRespSelected);
			vm.counselCompanySelected = _
					.without(vm.counselCompanySelected, '');
			vm.counselRespSelected = _.without(vm.counselRespSelected, '');
			vm.benchSelected = _.without(vm.benchSelected, '');
			vm.form.lastDateOfHearing = $filter('date')(
					vm.selitem.lastHearingDate, 'dd/MM/yyyy');
			vm.form.nextDateOfHearing = $filter('date')(
					vm.selitem.nextHearingDate, 'dd/MM/yyyy');
			vm.form.caseStatus = vm.selitem.statusID;

			var ldate = (vm.selitem.lastDt == undefined) ? new Date(
					'1900-01-01') : new Date(vm.selitem.lastDt);

			var day = ldate.getDate()
			var month = ldate.getMonth();
			var year = ldate.getFullYear()

			$(document)
					.ready(
							function() {

								$('#lastDateOfHearing').datepicker({
									format : 'dd/mm/yyyy',
									endDate : new Date()

								});

								$('#nextDateOfHearing').datepicker(
										{
											format : 'dd/mm/yyyy',
											startDate : new Date(year,
													parseInt(month),
													parseInt(day))

										});

								$('#lastDateOfHearing')
										.on(
												'changeDate',
												function(ev) {
													$('#lastDateOfHearing')
															.datepicker('hide');
													var val = $(
															'#lastDateOfHearing')
															.val();
													console.log(val);
													var split = val.split('/');
													var fullDt = split[2]
															+ '-'
															+ (split[1].length > 1 ? split[1]
																	: "0"
																			+ split[1])
															+ '-'
															+ (split[0].length > 1 ? split[0]
																	: "0"
																			+ split[0]);
													console.log(fullDt);
													var dt = new Date(fullDt);
													var day = dt.getDate();
													var month = dt.getMonth();
													var year = dt.getFullYear()

													$('#nextDateOfHearing')
															.datepicker(
																	{
																		format : 'dd/mm/yyyy',
																		startDate : new Date(
																				year,
																				parseInt(month),
																				parseInt(day))

																	});

												});

								$('#nextDateOfHearing').on(
										'changeDate',
										function(ev) {
											$('#nextDateOfHearing').datepicker(
													'hide');
										});

								if (vm.hearingHistory.length == 1) {
									document
											.getElementById('lastDateOfHearing').required = false;
									document.getElementById('outcomeLast').required = false;
									document.getElementById('bench').required = false;
									$('#lastDateOfHearing').prop('required',
											false);
									$('#outcomeLast').prop('required', false);
									$('#bench').prop('required', false);
								} else {
									document
											.getElementById('lastDateOfHearing').required = true;
									document.getElementById('outcomeLast').required = true;
									document.getElementById('bench').required = true;
									$('#lastDateOfHearing').prop('required',
											true);
									$('#outcomeLast').prop('required', true);
									$('#bench').prop('required', true);
								}

							});
$(function(){
			$('#lastDateOfHearing').val(vm.selitem.lastHearingDate);
			$('#nextDateOfHearing').val(vm.selitem.nextHearingDate);
			
			$('#formContainer').css('display', 'block');
})
			//$('#formContainer').collapse('show');

			vm.form.userID = vm.selitem.userID;

			// $('#saveBtn').text('Edit');

			$window.scrollTo(0, 0);
			// $('#viewContainer').animate({scrollTop : 0}, 'slow');

		}

		function cancelForm() {
			$(document).ready(function() {
				$('#formContainer').css('display', 'none');
				//$('#formContainer').collapse('hide');
			});
		}

		function disableHearingItem(item) {
			vm.selitem = vm.hearingHistory[item];

			if (vm.hearingHistory.length == 1) {
				alert('You are not allowed to delete this hearing information.');
				return false;
			}

			if (confirm('Are you sure to delete this hearing detail?')) {

				var jsonReq = {
					"caseID" : vm.caseID,
					"userID" : localStorageService.get('payrollNo'),
					'type' : 'delete',
					'id' : vm.selitem.id,
					'lastDateOfHearing' : vm.selitem.lastHearingDate,
					"sessID" : localStorageService.get('sessionID')
				}
				// alert(JSON.stringify(jsonReq));
				updatehearingService
						.updateHearing(jsonReq)
						.then(
								function(result) {

									if (result.data.status == 'T') {
										vm.edited = true;
										alert('Hearing details for HID '
												+ vm.selitem.id
												+ ' deleted successfully');
									$state.reload();

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

				return false;

		}

		function AddNewHearing() {
			vm.addFlag = true;

			if ((vm.caseItem.nextHearingDate == undefined || vm.caseItem.nextHearingDate == '')
					&& vm.hearingHistory.length > 1) {
				alert('Please update the hearing date in previous record');
				return false;
			}

			if (vm.caseItem.lastHearingDate == undefined
					|| vm.caseItem.lastHearingDate == '')
				$('#lastDateOfHearing').prop('disabled', false);
			else
				$('#lastDateOfHearing').prop('disabled', true);

			vm.outcomeLastHTML = '';
			vm.outcomeNextHTML = '';
			vm.form.outcomeLast = '';
			vm.form.outcomeNext = '';
			vm.form.nextDateOfHearing = '';
			vm.form.caseStatus = vm.caseItem.statusID;
			vm.form.bench = vm.caseItem.bench;
			vm.form.counselOfCompany = vm.caseItem.counselOfCompany;
			vm.form.counselOfRespondent = vm.caseItem.counselOfRespondent;

			if (vm.caseItem.bench != "" && vm.caseItem.bench != undefined) {
				var temp = vm.caseItem.bench.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.benchSelected.push(temp[i]);
			}

			if (vm.caseItem.counselOfCompany != ""
					&& vm.caseItem.counselOfCompany != undefined) {
				var temp = vm.caseItem.counselOfCompany.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.counselCompanySelected.push(temp[i]);
			}

			if (vm.caseItem.counselOfRespondent != ""
					&& vm.caseItem.counselOfRespondent != undefined) {
				var temp = vm.caseItem.counselOfRespondent.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.counselRespSelected.push(temp[i]);
			}
			vm.benchSelected = _.uniq(vm.benchSelected);
			vm.counselCompanySelected = _.uniq(vm.counselCompanySelected);
			vm.counselRespSelected = _.uniq(vm.counselRespSelected);
			vm.counselCompanySelected = _
					.without(vm.counselCompanySelected, '');
			vm.counselRespSelected = _.without(vm.counselRespSelected, '');
			vm.benchSelected = _.without(vm.benchSelected, '');
			vm.form.userID = vm.caseItem.userID;
			vm.form.lastDateOfHearing = $filter('date')(
					vm.caseItem.nextHearingDate, 'dd/MM/yyyy');

			var ldate = (vm.caseItem.lastDt == undefined) ? new Date(
					'1900-01-01') : new Date(vm.caseItem.lastDt);
			var day = ldate.getDate()
			var month = ldate.getMonth();
			var year = ldate.getFullYear()

			$(document).ready(
					function() {

						$('#lastDateOfHearing').datepicker({
							format : 'dd/mm/yyyy',
							endDate : new Date()

						});

						$('#nextDateOfHearing').datepicker(
								{
									format : 'dd/mm/yyyy',
									startDate : new Date(year, parseInt(month),
											parseInt(day))

								});

						$('#lastDateOfHearing').on(
								'changeDate',
								function(ev) {
									var val = $('#lastDateOfHearing').val();
									console.log(val);
									var split = val.split('/');

									var fullDt = split[2]
											+ '-'
											+ (split[1].length > 1 ? split[1]
													: "0" + split[1])
											+ '-'
											+ (split[0].length > 1 ? split[0]
													: "0" + split[0]);
									console.log(fullDt);
									var dt = new Date(fullDt)
									var day = dt.getDate()
									var month = dt.getMonth();
									var year = dt.getFullYear();

									$('#nextDateOfHearing').datepicker(
											{
												format : 'dd/mm/yyyy',
												startDate : new Date(year,
														parseInt(month),
														parseInt(day))

											});
									$('#lastDateOfHearing').datepicker('hide');
								});

						$('#nextDateOfHearing').on('changeDate', function(ev) {
							$('#nextDateOfHearing').datepicker('hide');
						});

					});
			$('#formContainer').css('display', 'block');

			//$('#formContainer').collapse('show');

			// $('#saveBtn').text('Add Hearing');
			$window.scrollTo(0, 0);
		}

		function openBenchModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'bench.html',
				size : 'md',
				backdrop : 'static',
				keyboard : false,
				windowClass : 'userFillWindow',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function openCounselRespModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'counselOfResp.html',
				size : 'md',
				backdrop : 'static',
				keyboard : false,
				windowClass : 'userFillWindow',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function openCounselCompModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'counselOfCompany.html',
				size : 'md',
				backdrop : 'static',
				keyboard : false,
				windowClass : 'userFillWindow',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function outcomeLastModal() {
			vm.outcomeLastHTML = vm.form.outcomeLast;
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'outcomeLast.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				vm.form.outcomeLast = vm.outcomeLastHTML;
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function outcomeNextModal() {
			vm.outcomeNextHTML = vm.form.outcomeNext;
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'outcomeNext.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				vm.form.outcomeNext = vm.outcomeNextHTML;
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function AddMember(modalType) {
			if (modalType == 'bench') {
				if (_.find(vm.benchInfo, function(item) {
					return item.name == vm.selBench;
				})) {
					vm.benchSelected.push(vm.selBench);
					vm.selBench = '';
					console.log('true');
				} else if (vm.selBench != '') {
					if (confirm('Are you sure to add ' + vm.selBench
							+ ' as bench member')) {
						var jsonReq = JSON.stringify({
							'type' : 'bench',
							'name' : vm.selBench,
							'userID' : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member ' + vm.selBench
														+ ' added successfully');
												vm.benchSelected
														.push(vm.selBench);
												vm.selBench = '';

											} else {
												alert(result.data.msg);
												return false;
											}
										},
										function loginError(error) {

											alert('Something went wrong... Pls try again...');
											return false;
										});

					} else {
						console.log('false');
					}
				}

			} else if (modalType == 'councelCompany') {
				if (_.find(vm.counselCompanyInfo, function(item) {
					return item.name == vm.selCounselCompany;
				})) {
					vm.counselCompanySelected.push(vm.selCounselCompany);
					vm.selCounselCompany = '';

				} else if (vm.selCounselCompany != '') {
					if (confirm('Are you sure to add ' + vm.selCounselCompany
							+ ' as counsel member')) {
						var jsonReq = JSON.stringify({
							'type' : 'councelCompany',
							'name' : vm.selCounselCompany,
							'userID' : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member '
														+ vm.selCounselCompany
														+ ' added successfully');
												vm.counselCompanySelected
														.push(vm.selCounselCompany);
												vm.selCounselCompany = '';

											} else {
												alert(result.data.msg);
												return false;
											}
										},
										function loginError(error) {

											alert('Something went wrong... Pls try again...');
											return false;
										});

					} else {
						console.log('false');
					}
				}

			} else if (modalType == 'councelResp') {
				if (_.find(vm.counselRespInfo, function(item) {
					return item.name == vm.selCounselResp;
				})) {
					vm.counselRespSelected.push(vm.selCounselResp);
					vm.selCounselResp = '';

				} else if (vm.selCounselResp != '') {
					if (confirm('Are you sure to add ' + vm.selCounselResp
							+ ' as counsel member')) {
						var jsonReq = JSON.stringify({
							'type' : 'councelCompany',
							'name' : vm.selCounselResp,
							'userID' : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member '
														+ vm.selCounselResp
														+ ' added successfully');
												vm.counselRespSelected
														.push(vm.selCounselResp);
												vm.selCounselResp = '';

											} else {
												alert(result.data.msg);
												return false;
											}
										},
										function loginError(error) {

											alert('Something went wrong... Pls try again...');
											return false;
										});

					} else {
						console.log('false');
					}
				}

			}
		}

		function removeMember(item, modalType) {
			if (modalType == 'bench')
				vm.benchSelected.splice(item, 1);
			else if (modalType == 'councelCompany')
				vm.counselCompanySelected.splice(item, 1);
			else if (modalType == 'councelResp')
				vm.counselRespSelected.splice(item, 1);

		}

		function diplayMember(modalType) {
			if (modalType == 'bench')
				vm.form.bench = vm.benchSelected.toString();
			else if (modalType == 'councelCompany')
				vm.form.counselOfCompany = vm.counselCompanySelected.toString();
			else if (modalType == 'councelResp')
				vm.form.counselOfRespondent = vm.counselRespSelected.toString();

			vm.counselCompanySelected = _
					.without(vm.counselCompanySelected, '');
			vm.counselRespSelected = _.without(vm.counselRespSelected, '');
			vm.benchSelected = _.without(vm.benchSelected, '');
			$uibModalStack.dismissAll();
		}

	}

})();
