(function() {
	'use strict';

	angular.module('regCalApp.editcase').controller('EditCaseController',
			EditCaseController);

	EditCaseController.$inject = [ '$state', 'EditCaseService', '$scope',
			'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
			'SearchCaseService', 'dashboardService', '$uibModal',
			'$uibModalStack', 'addCaseService', '$window', 'ManageForumService' ];

	/* @ngInject */
	function EditCaseController($state, EditCaseService, $scope,
			localStorageService, DTOptionsBuilder, DTColumnBuilder,
			SearchCaseService, dashboardService, $uibModal, $uibModalStack,
			addCaseService, $window, ManageForumService) {
		var vm = angular.extend(this, {

			form : {
				company : '',
				business : '',
				vertical : '',
				forum : '',
				caseNo : '',
				caseYear : 0,
				courtCaseType : '',
				forumCategory : '',
				petitioner : '',
				respondent : '',
				bench : '',
				aorOfCompany : '',
				counselOfCompany : '',
				aorOfRespondent : '',
				counselOfRespondent : '',
				lastDateOfHearing : '',
				nextDateOfHearing : '',
				furtherDates : '',
				businessRep : '',
				legalRep : '',
				caseStatus : '',
				subMatter : '',
				briefFacts : '',
				interimPrayer : '',
				finalPrayer : '',
				outcomeLast : '',
				outcomeNext : '',
				finImpact : '',
				caseCategory : '',
				caseType : '',
				assessYr : '',
				finYear : '',
				finImpactDetailed : '',
				amtOfDisallow : 0,
				state : 0,
				assetSegment : '',
				trustName : '',
				accountName : 0,
				substitutionFiled : '',
				substitutionAllowed : '',
				stayOrder : '',
				interimStay : '',
				finImpactRecurring : '',
				finImpactRecurringDuration : ''

			},
			submit : submit,
			factsHTML : '',
			intrimPHTML : '',
			finalPHTML : '',
			outcomeLastHTML : '',
			outcomeNextHTML : '',
			openBenchModal : openBenchModal,
			openAorCompModal : openAorCompModal,
			openCounselCompModal : openCounselCompModal,
			openBusinessRepModal : openBusinessRepModal,
			openLegalRepModal : openLegalRepModal,
			openSubjectModal : openSubjectModal,
			openFactsModal : openFactsModal,
			interimPModal : interimPModal,
			finalPModal : finalPModal,
			outcomeLastModal : outcomeLastModal,
			outcomeNextModal : outcomeNextModal,
			moveMember : moveMember,
			benchSelected : [],
			aorCompanySelected : [],
			counselCompanySelected : [],
			businessRepSelected : [],
			legalRepSelected : [],
			AddMember : AddMember,
			removeMember : removeMember,
			diplayMember : diplayMember,
			forumInfo : [],
			benchInfo : [],
			businessRepInfo : [],
			legalRepInfo : [],
			caseStatusInfo : [],
			caseCategoryInfo : [],
			aorCompanyInfo : [],
			counselCompanyInfo : [],
			selBusiness : '',
			selBench : '',
			selAorCompany : '',
			selCounselCompany : '',
			businessRep : '',
			legalRep : '',
			selPetitioner : '',
			petitionerSelected : [],
			selRespondent : '',
			respondentSelected : [],
			openPetitionerModal : openPetitionerModal,
			openRespondentModal : openRespondentModal,
			showMultiplePicker : showMultiplePicker,
			years : [],
			yearSel : false,
			checkYear : checkYear,
			openAorRespModal : openAorRespModal,
			selAorResp : '',
			aorRespSelected : [],
			openCounselRespModal : openCounselRespModal,
			selCounselResp : '',
			counselRespSelected : [],
			aorRespInfo : [],
			counselRespInfo : [],
			financialImpactModal : financialImpactModal,
			finImp : [],
			newFinDetail : newFinDetail,
			numsForPage : [ 5, 10, 25, 50, 100 ],
			currentPage : 1,
			maxSize : 5,
			filteredTodos : [],
			numPerPage : 10,
			expenseedit : false,
			newexpense : {},
			curExpense : {},
			changeNum : changeNum,
			editExpense : editExpense,
			addExpense : addExpense,
			cancelExpense : cancelExpense,
			deleteExpense : deleteExpense,
			filteredlist : [],
			currency : [ 'INR', 'USD' ],
			setTotal : setTotal,
			closeFinImp : closeFinImp,
			grandTotal : 0,
			fetchAssessYear : fetchAssessYear,
			goBack : goBack,
			stateInfo : [],
			outcomeLast : '',
			outcomeNext : '',
			forumCategoryInfo : [],
			caseYearInfo : [],
			fetchForumInfo : fetchForumInfo,
			fetchCaseTypeInfo : fetchCaseTypeInfo,
			fetchCaseTypeArray : [],
			checkCaseNo : checkCaseNo,
			setError : false,
			inputFormatter : inputFormatter,

			assetSegmantInfo : [],
			trustNameInfo : [],
			accountNameInfo : [],
			fetchTrustName : fetchTrustName,
			fetchAccountName : fetchAccountName,
			resultArray : [],
			resultArrayInterim : []

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			var d = new Date();
			var n = d.getFullYear();
			var next = n + 1;
			for (var i = 1990; i <= next; i++) {
				var t = i + 1;
				vm.years.push(i + '-' + t);
			}

			for (var i = n; i >= 1980; i--) {
				vm.caseYearInfo.push(i);
			}
			vm.resultArray = [ 'Yes', 'No', 'NA' ]
			vm.resultArrayInterim = [ 'Yes', 'No' ]
			//			 vm.caseDetails = SearchCaseService.getCaseDetails();
			//			 if(vm.caseDetails == "")
			//				 vm.caseDetails = localStorageService.get('caseDetails');
			//			 	var caseDetails1 = SearchCaseService.getCaseDetails();
			vm.caseDetails = localStorageService.get('viewDetails');

			//angular.extend(vm.caseDetails, caseD, caseDetails2);
			vm.caseID = vm.caseDetails.caseID;
			vm.form.company = vm.caseDetails.companyAlias;
			vm.form.business = vm.caseDetails.business;
			vm.form.vertical = vm.caseDetails.vertical;

			vm.form.caseType = vm.caseDetails.caseTypeID;
			vm.form.state = vm.caseDetails.stateID;
			vm.form.refNo = vm.caseDetails.refNo;
			//vm.form.caseNo = vm.caseDetails.caseNo;
			vm.form.petitioner = vm.caseDetails.petitioner;
			vm.form.respondent = vm.caseDetails.respondent;
			vm.form.aorOfRespondent = vm.caseDetails.aorOfRespondent;
			vm.form.counselOfRespondent = vm.caseDetails.counselOfRespondent;
			vm.form.lastDateOfHearing = vm.caseDetails.lastHearingDate;
			vm.form.nextDateOfHearing = vm.caseDetails.nextHearingDate;
			vm.form.furtherDates = vm.caseDetails.furtherDates;
			vm.petitionerHTML = vm.caseDetails.petitioner;
			vm.respondentHTML = vm.caseDetails.respondent;
			vm.form.subMatter = vm.caseDetails.subMatter;
			vm.subMatterHTML = vm.caseDetails.subMatter;
			vm.factsHTML = vm.caseDetails.briefFacts;
			vm.intrimPHTML = vm.caseDetails.interimPrayer;
			vm.finalPHTML = vm.caseDetails.finalPrayer;
			vm.outcomeLastHTML = vm.caseDetails.outcomeLast;
			vm.outcomeNextHTML = vm.caseDetails.outcomeNext;
			vm.outcomeLast = vm.caseDetails.outcomeLast;
			vm.outcomeNext = vm.caseDetails.outcomeNext;
			vm.form.briefFacts = vm.caseDetails.briefFacts;
			vm.form.interimPrayer = vm.caseDetails.interimPrayer;
			vm.form.finalPrayer = vm.caseDetails.finalPrayer;
			vm.form.outcomeLast = vm.caseDetails.outcomeLast;
			vm.form.outcomeNext = vm.caseDetails.outcomeNext;
			vm.form.finImpact = vm.caseDetails.finImpact;
			vm.form.finImpactRecurring = vm.caseDetails.finImpRecurring;
			vm.form.finImpactRecurringDuration = vm.caseDetails.finImaRecurringDuration;
			vm.form.bench = vm.caseDetails.bench;
			vm.form.aorOfCompany = vm.caseDetails.aorOfCompany;
			vm.form.counselOfCompany = vm.caseDetails.counselOfCompany;
			vm.form.businessRep = vm.caseDetails.businessRep;
			vm.form.legalRep = vm.caseDetails.legalRep;
			vm.caseTypeID = vm.caseDetails.caseTypeID;

			if (vm.form.outcomeLast != '')
				$('#outcomeLast').css('background', '#eee');
			else
				$('#outcomeLast').css('background', 'white');

			if (vm.form.outcomeNext != '')
				$('#outcomeNext').css('background', '#eee');
			else
				$('#outcomeNext').css('background', 'white');

			if (vm.caseDetails.aorOfCompany != "") {
				var temp = vm.caseDetails.aorOfCompany.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.aorCompanySelected.push(temp[i]);
			}

			if (vm.caseDetails.aorOfRespondent != "") {
				var temp = vm.caseDetails.aorOfRespondent.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.aorRespSelected.push(temp[i]);
			}

			if (vm.caseDetails.businessRep != "") {
				var temp = vm.caseDetails.businessRep.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.businessRepSelected.push(temp[i]);
			}
			if (vm.caseDetails.legalRep != "") {
				var temp = vm.caseDetails.legalRep.split(',');
				for (var i = 0; i < temp.length; i++)
					vm.legalRepSelected.push(temp[i]);
			}

			vm.aorRespSelected = _.uniq(vm.aorRespSelected);
			vm.businessRepSelected = _.uniq(vm.businessRepSelected);
			vm.legalRepSelected = _.uniq(vm.legalRepSelected);
			vm.aorCompanySelected = _.uniq(vm.aorCompanySelected);
			vm.aorRespSelected = _.without(vm.aorRespSelected, '');
			vm.businessRepSelected = _.without(vm.businessRepSelected, '');
			vm.legalRepSelected = _.without(vm.legalRepSelected, '');
			vm.aorCompanySelected = _.without(vm.aorCompanySelected, '');

			vm.businessRep = vm.caseDetails.businessRep;
			vm.legalRep = vm.caseDetails.legalRep;
			vm.form.caseStatus = vm.caseDetails.statusName;
			vm.form.caseCategory = vm.caseDetails.categoryID;
			vm.form.userID = vm.caseDetails.userID;

			var a = $(vm.petitionerHTML);
			(a.find('li')).each(function(idx, li) {
				vm.petitionerSelected.push($(li).text());

			});
			var b = $(vm.respondentHTML);
			(b.find('li')).each(function(idx, li) {
				vm.respondentSelected.push($(li).text());

			});

			if (vm.caseDetails.caseTypeID == 1) {
				$('#fieldSetId').css('display', 'block');
				$('#caseTypeCont').css('display', 'flex');
				vm.yearSel = true;
				vm.form.assessYear = vm.caseDetails.assessYear;
				vm.form.finYear = vm.caseDetails.finYear;
				vm.form.amtOfDisallow = vm.caseDetails.amtOfDisallow;
			} else if (vm.caseDetails.companyID == 3
					&& vm.caseDetails.businessID == 18
					&& vm.caseDetails.verticalID == 99) {
				$('#fieldSetId').css('display', 'block');
				$('#caseTypeCont').css('display', 'none');
				vm.yearSel = false;
			} else {
				$('#fieldSetId').css('display', 'none');
				$('#caseTypeCont').css('display', 'none');
				vm.yearSel = false;
				vm.form.assessYear = '';
				vm.form.finYear = '';
				vm.form.amtOfDisallow = '';
			}
			if (localStorageService.get("itemPerPage") === null) {
				localStorageService.set("itemPerPage", 10);
			}
			if (vm.caseDetails.companyID == 3
					&& vm.caseDetails.businessID == 18
					&& vm.caseDetails.verticalID == 99) {

				var jsonReq = {
					"type" : "fetchAssetSegment",
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				//alert(JSON.stringify(jsonReq))
				addCaseService.fetchMastersForAsset(jsonReq).then(
						function(result) {
							if(result.data.status == 'F'){
								alert(result.data.msg);
								return false;
							}
							vm.assetSegmantInfo = result.data.assetNameArray;

						}, function loginError(error) {

							alert('Something went wrong... Pls try again...');
							return false;
						});

				vm.form.assetSegment = vm.caseDetails.assetSegment
				if (vm.form.assetSegment != undefined
						&& vm.form.assetSegment != '')
					fetchTrustName(vm.form.assetSegment)
				vm.form.trustName = vm.caseDetails.trustName;
				if (vm.form.trustName != undefined && vm.form.trustName != '')
					fetchAccountName(vm.form.trustName)
				vm.form.accountName = vm.caseDetails.accountID
				vm.form.substitutionFiled = vm.caseDetails.substitutionFiled
				vm.form.substitutionAllowed = vm.caseDetails.substitutionAllowed
				vm.form.stayOrder = vm.caseDetails.stayOrder
				vm.form.interimStay = vm.caseDetails.interimStay

				//outJson.put("accountName", rs.getString(62));
			}

			vm.numPerPage = getPerPage();
			if (vm.caseDetails.finImpArray.length > 0) {
				vm.curPg = vm.caseDetails.finImpArray[0].currency;
				vm.finImp = vm.caseDetails.finImpArray;
				vm.filteredlist = vm.finImp;

				_.each(vm.caseDetails.finImpArray, function(item, index) {
					vm.grandTotal += item.total;
				})
				vm.form.finImpactDetailed = vm.grandTotal;
			}

			getCaseDetails();
			vm.form.forumCategory = vm.caseDetails.forumCategory;
			fetchForumInfo(vm.caseDetails.forumCategory);

			fetchCaseTypeInfo(vm.caseDetails.forumID);
			if (vm.caseDetails.caseTypeNum != 0
					&& vm.caseDetails.caseTypeNum != undefined)
				vm.form.courtCaseType = vm.caseDetails.caseTypeNum;
			else
				vm.form.courtCaseType = 1000000;
			if (vm.caseDetails.courtCaseID != undefined
					&& vm.caseDetails.courtCaseID != '') {
				vm.form.caseNo = vm.caseDetails.courtCaseID;
				vm.form.caseYear = vm.caseDetails.caseYear;
				vm.setError = false;
			} else if (vm.caseDetails.forumID > 100000) {
				vm.form.caseNo = vm.caseDetails.caseNo;
				vm.setError = false;
			} else {
				vm.form.caseNo = vm.caseDetails.caseNo;
				vm.setError = true;
			}
			$('#state').val(vm.caseDetails.stateID);

		})();

		function goBack() {
			$window.close();
			// $state.go('viewcase');

		}

		function fetchTrustName(item) {

			var jsonReq = {
				"type" : "fetchTrustName",
				"assetSegment" : item,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			//alert(JSON.stringify(jsonReq))
			addCaseService.fetchMastersForAsset(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.trustNameInfo = result.data.trustNameArray;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		function fetchAccountName(item) {
			var jsonReq = {
				"type" : "fetchAccountName",
				"assetSegment" : vm.form.assetSegment,
				"trustName" : item,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			//alert(JSON.stringify(jsonReq))
			addCaseService.fetchMastersForAsset(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.accountNameInfo = result.data.accountNameArray;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		function inputFormatter($model, item) {
			if (!$model)
				return "";
			else if (typeof $model == 'number') {
				var val = _.findWhere(vm.forumInfo, {
					id : $model
				})
				return val.name;
			} else {
				var val = _.findWhere(vm.forumInfo, {
					id : $model.id
				})
				return val.name;
			}
		}

		function checkCaseNo(item) {

			if (vm.form.forum >= 100000) {
				$('#caseNo').removeAttr('num-only')
				$('#caseNo').removeAttr('min')
			} else {
				if (item == undefined || item.match(/^\d+$/))
					vm.setError = false;
				else
					vm.setError = true;
				vm.form.caseNo = vm.form.caseNo.replace(/[^\d]/g, '');
				$('#caseNo').attr('num-only', '')
				$('#caseNo').attr('min', '0')
			}
		}

		function financialImpactModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'finImpact.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

		}

		function fetchForumInfo(item) {
			vm.form.forum = '';
			var jsonReq = {
				"type" : "fetchForum",
				"category" : item,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				console.log(result.data);
				vm.forumInfo = result.data.resultArray;
				vm.form.forum = vm.caseDetails.forumID;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		function fetchCaseTypeInfo(item) {
			var jsonReq = {
				"type" : "fetchCaseType",
				"forumID" : item,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				console.log(result.data);
				vm.fetchCaseTypeArray = result.data.resultArray;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		function setTotal() {
			vm.form.finImpactDetailed = $('#grandTotal').text();
			$uibModalStack.dismissAll();
		}
		function closeFinImp() {
			vm.finImp = [];
			$uibModalStack.dismissAll();
		}
		function getPerPage() {
			return parseInt(localStorageService.get('itemPerPage'));
		}

		function changeNum(itemNum) {
			localStorageService.itemPerPage = itemNum;
			vm.numPerPage = getPerPage();
		}

		function editExpense(thisExp) {
			vm.expenseedit = true;
			thisExp.principal = parseInt(thisExp.principal)
			thisExp.penalty = parseInt(thisExp.penalty)
			thisExp.interest = parseInt(thisExp.interest)
			thisExp.total = parseInt(thisExp.principal)
					+ parseInt(thisExp.penalty) + parseInt(thisExp.interest);
			vm.curExpense = thisExp;
			vm.newexpense = angular.copy(thisExp);

		}
		function deleteExpense(item) {
			var confirmDelete = confirm("Do you really need to delete this entry ?");
			if (confirmDelete) {
				var curIndex = vm.finImp.indexOf(item);
				vm.finImp.splice(curIndex, 1);
			}
		}

		function addExpense() {

			if (vm.curExpense.id) {
				vm.newexpense.total = parseInt(vm.newexpense.principal)
						+ parseInt(vm.newexpense.penalty)
						+ parseInt(vm.newexpense.interest)
				angular.copy(vm.newexpense, vm.curExpense);
				//angular.extend(vm.curExpense, vm.curExpense, vm.newexpense);
			} else {
				vm.newexpense.id = vm.finImp.length + 1;
				vm.newexpense.total = parseInt(vm.newexpense.principal)
						+ parseInt(vm.newexpense.penalty)
						+ parseInt(vm.newexpense.interest)
				vm.finImp.push(vm.newexpense);
			}
			vm.expenseedit = false;
			vm.newexpense = {};

		}
		function cancelExpense() {
			vm.expenseedit = false;
			vm.newexpense = {};
			vm.curExpense = {};
		}
		function newFinDetail(argument) {
			vm.expenseedit = true;
			vm.newexpense = {};
			vm.curExpense = {};
		}

		function fetchAssessYear() {
			var finYear = $('#finYear').val();
			var arr = finYear.split(':');
			var temp = arr[1].split('-');
			var temp1 = parseInt(temp[0]) + 1
			var temp2 = parseInt(temp[1]) + 1
			var assesYear = temp1 + '-' + temp2;
			$('#assessYear').val(assesYear);
			vm.form.assessYear = assesYear;
		}

		function checkYear() {
			var sel = $('#caseType').val();
			var arr = sel.split(':');

			if (arr[1] == 1) {
				$('#fieldSetId').css('display', 'block');
				$('#caseTypeCont').css('display', 'flex');

				vm.yearSel = true;
			} else if (vm.caseDetails.companyID == 3
					&& vm.caseDetails.businessID == 18
					&& vm.caseDetails.verticalID == 99) {
				$('#fieldSetId').css('display', 'block');
				$('#caseTypeCont').css('display', 'none');
				vm.yearSel = false;
			} else {
				$('#fieldSetId').css('display', 'none');
				$('#caseTypeCont').css('display', 'none');

				vm.yearSel = false;
			}
		}
		function getCaseDetails() {

			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"type" : "fetchMasterForEdit",

				"sessID" : localStorageService.get('sessionID')
			}
			addCaseService.fetchAllMasters(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}

				vm.forumCategoryInfo = result.data.forumCategoryMaster;
				vm.aorCompanyInfo = result.data.aorCompanyMaster;
				vm.aorRespInfo = result.data.aorCompanyMaster;
				vm.caseCategoryInfo = result.data.categoryMaster;
				vm.caseTypeInfo = result.data.caseTypeMaster;
				vm.stateInfo = result.data.stateArray;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"compID" : vm.caseDetails.companyID.toString(),
				"type" : "fetchBusiness",
				"sessID" : localStorageService.get('sessionID')
			}
			addCaseService.fetchAllMasters(jsonReq).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.businessInfo = result.data.businessMaster;
				vm.businessRepInfo = result.data.businessRepMaster;
				vm.legalRepInfo = result.data.legalRepMaster;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

			//			dashboardService.fetchAllMaster()
			//    		.then(function(result) {    			
			//    			vm.benchInfo = result.data.benchMaster;    			
			//    			vm.aorCompanyInfo = result.data.aorCompanyMaster;
			//    			vm.counselCompanyInfo = result.data.counselCompanyMaster;
			//    			vm.caseStatusInfo = result.data.statusMaster;
			//    			vm.caseCategoryInfo = result.data.categoryMaster;
			//    			vm.forumInfo = result.data.forumMaster;
			//    			vm.caseTypeInfo = result.data.caseTypeMaster;
			//    			vm.stateInfo = result.data.stateArray;
			//    			vm.stateInfo.push({'id' : '0', 'name' : ''});
			//			}, function caseError(error){
			//		      alert('There seems some problem. Please try again later...');
			//		      return false;
			//		    });
			//			dashboardService.fetchCompanyMaster(vm.caseDetails.companyID)
			//    		.then(function(result) {
			//    			
			//    			
			//    			vm.businessRepInfo = result.data.businessRepMaster;
			//    			vm.legalRepInfo = result.data.legalRepMaster;
			//			
			//			}, function caseError(error){
			//		      alert('There seems some problem. Please try again later...');
			//		      return false;
			//		    });

		}
		function showMultiplePicker() {
			var myEl = angular.element(document.querySelector('#furtherDates'));
			myEl.attr('multiple-date-picker', "");
		}

		function openBenchModal() {
			if (vm.benchSelected != 0)
				return false;
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

		function openPetitionerModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'petitioner.html',
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

		function openRespondentModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'respondent.html',
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

		function openSubjectModal() {

			vm.subMatterHTML = vm.form.subMatter;

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'summary.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {

				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function openFactsModal() {
			vm.factsHTML = vm.form.briefFacts;
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'facts.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {

				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function interimPModal() {
			vm.intrimPHTML = vm.form.interimPrayer;
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'interim.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {

				console.log('Modal dismissed at: ' + new Date());
			});
		}
		function finalPModal() {
			vm.finalPHTML = vm.form.finalPrayer;
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'final.html',
				size : 'lg',
				backdrop : 'static',
				keyboard : false,
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

				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function openAorCompModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'aorCompany.html',
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

		function openAorRespModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'aorResp.html',
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
			if (vm.counselCompanySelected != 0)
				return false;
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

		function openCounselRespModal() {
			if (vm.counselRespSelected != 0)
				return false;
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

		function openBusinessRepModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'businessRep.html',
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

		function openLegalRepModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'legalRep.html',
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

		function removeMember(item, modalType) {
			if (modalType == 'bench')
				vm.benchSelected.splice(item, 1);
			else if (modalType == 'aorCompany')
				vm.aorCompanySelected.splice(item, 1);
			else if (modalType == 'councelCompany')
				vm.counselCompanySelected.splice(item, 1);
			else if (modalType == 'aorResp')
				vm.aorRespSelected.splice(item, 1);
			else if (modalType == 'councelResp')
				vm.counselRespSelected.splice(item, 1);
			else if (modalType == 'businessRep')
				vm.businessRepSelected.splice(item, 1);
			else if (modalType == 'legalRep')
				vm.legalRepSelected.splice(item, 1);
			else if (modalType == 'petitioner')
				vm.petitionerSelected.splice(item, 1);
			else if (modalType == 'respondent')
				vm.respondentSelected.splice(item, 1);

		}

		function moveMember(item, modalType, moveType) {
			if (item = 0)
				return false;
			if (modalType == 'petitioner') {
				if (item == vm.petitionerSelected.length)
					return false;
				if (moveType == 'up') {
					move(vm.petitionerSelected, item, item - 1);
				} else if (moveType == 'down') {
					move(vm.petitionerSelected, item, item + 1);
				}
			} else if (modalType == 'respondent') {
				if (item == vm.respondentSelected.length)
					return false;
				if (moveType == 'up') {
					move(vm.respondentSelected, item, item - 1);
				} else if (moveType == 'down') {
					move(vm.respondentSelected, item, item + 1);
				}
			}
		}

		function diplayMember(modalType) {
			if (modalType == 'bench')
				vm.form.bench = vm.benchSelected.toString();
			else if (modalType == 'aorCompany')
				vm.form.aorOfCompany = vm.aorCompanySelected.toString();
			else if (modalType == 'councelCompany')
				vm.form.counselOfCompany = vm.counselCompanySelected.toString();
			else if (modalType == 'aorResp')
				vm.form.aorOfRespondent = vm.aorRespSelected.toString();
			else if (modalType == 'councelResp')
				vm.form.counselOfRespondent = vm.counselRespSelected.toString();
			else if (modalType == 'businessRep')
				vm.form.businessRep = vm.businessRepSelected.toString();
			else if (modalType == 'legalRep')
				vm.form.legalRep = vm.legalRepSelected.toString();
			else if (modalType == 'petitioner') {
				var html = '';
				var ind = 0;
				_.each(vm.petitionerSelected, function(name, index) {
					ind = index + 1;
					if ((name.substring(0, 1) == 'P')
							&& (name.indexOf('.') > 0)) {
						var temp = name.split('.');
						name = temp[1];
						html += '<li>P' + ind + '.' + name + '</li>';
					} else
						html += '<li>P' + ind + '.' + name + '</li>';
				});
				vm.petitionerHTML = '<ol class="myUL" >' + html + '</ol>';
			} else if (modalType == 'respondent') {
				var html1 = '';
				var ind = 0;
				_.each(vm.respondentSelected, function(name, index) {
					ind = index + 1;
					if ((name.substring(0, 1) == 'R')
							&& (name.indexOf('.') > 0)) {
						var temp = name.split('.');
						name = temp[1];
						html += '<li>R' + ind + '.' + name + '</li>';
					} else
						html1 += '<li>R' + ind + '.' + name + '</li>';
				});
				vm.respondentHTML = '<ol class="myUL" >' + html1 + '</ol>';
			}

			$uibModalStack.dismissAll();
		}

		function move(arr, old_index, new_index) {
			while (old_index < 0) {
				old_index += arr.length;
			}
			while (new_index < 0) {
				new_index += arr.length;
			}
			if (new_index >= arr.length) {
				var k = new_index - arr.length;
				while ((k--) + 1) {
					arr.push(undefined);
				}
			}
			arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
			//return arr;
		}

		function AddMember(modalType) {
			if (modalType == 'bench') {
				if (_.find(vm.benchInfo, function(item) {
					return item.name == vm.selBench;
				})) {
					vm.benchSelected.push(vm.selBench);
					vm.selBench = '';
					console.log('true');
				} else {
					if (confirm('Are you sure to add ' + vm.selBench
							+ ' as bench member')) {
						var jsonReq = JSON.stringify({
							'type' : 'bench',
							'name' : vm.selBench,
							"userID" : localStorageService.get('payrollNo'),
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

			} else if (modalType == 'aorCompany') {
				if (_.find(vm.aorCompanyInfo, function(item) {
					return item.name == vm.selAorCompany;
				})) {
					vm.aorCompanySelected.push(vm.selAorCompany);
					vm.selAorCompany = '';

				} else {
					if (confirm('Are you sure to add ' + vm.selAorCompany
							+ ' as AOR member')) {
						var jsonReq = JSON.stringify({
							'type' : 'aorCompany',
							'name' : vm.selAorCompany,
							"userID" : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member '
														+ vm.selAorCompany
														+ ' added successfully');
												vm.aorCompanySelected
														.push(vm.selAorCompany);
												vm.selAorCompany = '';

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

			} else if (modalType == 'aorResp') {
				if (_.find(vm.aorRespInfo, function(item) {
					return item.name == vm.selAorResp;
				})) {
					vm.aorRespSelected.push(vm.selAorResp);
					vm.selAorResp = '';

				} else {
					if (confirm('Are you sure to add ' + vm.selAorResp
							+ ' as AOR member')) {
						var jsonReq = JSON.stringify({
							'type' : 'aorCompany',
							'name' : vm.selAorResp,
							"userID" : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member ' + vm.selAorResp
														+ ' added successfully');
												vm.aorRespSelected
														.push(vm.selAorResp);
												vm.selAorResp = '';

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

				} else {
					if (confirm('Are you sure to add ' + vm.selCounselCompany
							+ ' as counsel member')) {
						var jsonReq = JSON.stringify({
							'type' : 'councelCompany',
							'name' : vm.selCounselCompany,
							"userID" : localStorageService.get('payrollNo'),
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

				} else {
					if (confirm('Are you sure to add ' + vm.selCounselResp
							+ ' as counsel member')) {
						var jsonReq = JSON.stringify({
							'type' : 'councelCompany',
							'name' : vm.selCounselResp,
							"userID" : localStorageService.get('payrollNo'),
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

			} else if (modalType == 'businessRep') {
				if (_.find(vm.businessRepInfo, function(item) {
					return item.name == vm.businessRep;
				})) {
					vm.businessRepSelected.push(vm.businessRep);
					vm.businessRep = '';

				} else {
					if (confirm('Are you sure to add ' + vm.businessRep
							+ ' as business representative member')) {
						var jsonReq = JSON.stringify({
							'type' : 'businessRep',
							'name' : vm.businessRep,
							"userID" : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID'),
							'company' : parseInt(vm.form.company)
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member '
														+ vm.businessRep
														+ ' added successfully');
												vm.businessRepSelected
														.push(vm.businessRep);
												vm.businessRep = '';

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

			} else if (modalType == 'legalRep') {
				if (_.find(vm.legalRepInfo, function(item) {
					return item.name == vm.legalRep;
				})) {
					vm.legalRepSelected.push(vm.legalRep);
					vm.legalRep = '';

				} else {
					if (confirm('Are you sure to add ' + vm.legalRep
							+ ' as legal representative member')) {
						var jsonReq = JSON.stringify({
							'type' : 'legalRep',
							'company' : parseInt(vm.form.company),
							'name' : vm.legalRep,
							"userID" : localStorageService.get('payrollNo'),
							"sessID" : localStorageService.get('sessionID')
						});
						addCaseService
								.addNewMaster(jsonReq)
								.then(
										function(result) {

											if (result.data.status == 'T') {
												alert('Member ' + vm.legalRep
														+ ' added successfully');
												vm.legalRepSelected
														.push(vm.legalRep);
												vm.legalRep = '';

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

			} else if (modalType == 'petitioner') {

				vm.petitionerSelected.push(vm.selPetitioner);
				vm.selPetitioner = '';
			} else if (modalType == 'respondent') {

				vm.respondentSelected.push(vm.selRespondent);
				vm.selRespondent = '';
			}

		}

		function submit(form) {
			angular.forEach(form, function(obj) {
				if (angular.isObject(obj) && angular.isDefined(obj.$setDirty)) {
					//					if(obj.$name == 'caseNo' && obj.$viewValue == ''){
					//						console.log(true)
					//					}else
					obj.$setDirty();
				}
			})

			if (form.$valid) {
				var val = '';
				if (!form.forum.$modelValue)
					val = '';
				else if (typeof form.forum.$modelValue == 'number') {
					var test = _.findWhere(vm.forumInfo, {
						id : form.forum.$modelValue
					})
					val = test.id;
				} else {
					var test = _.findWhere(vm.forumInfo, {
						id : form.forum.$modelValue.id
					})
					val = test.id;
				}

//				if (!(form.caseNo.$modelValue.match(/^\d+$/)) && val <= 100000) {
//					vm.setError = true;
//					return false;
//				}

				var jsonReq = {
					"company" : vm.caseDetails.companyID,
					"business" : vm.caseDetails.businessID,
					"vertical" : vm.caseDetails.verticalID,
					"caseType" : form.caseType.$modelValue,
					"forum" : val,
					"caseNo" : form.caseNo.$modelValue,
					"petitioner" : vm.petitionerHTML,
					"respondent" : vm.respondentHTML,
					"bench" : form.bench.$modelValue,
					"aorOfCompany" : form.aorOfCompany.$modelValue,
					"counselOfCompany" : form.counselOfCompany.$modelValue,
					"aorOfRespondent" : form.aorOfRespondent.$modelValue,
					"counselOfRespondent" : form.counselOfRespondent.$modelValue,
					"lastDateOfHearing" : form.lastDateOfHearing.$modelValue,
					"nextDateOfHearing" : form.nextDateOfHearing.$modelValue,
					"furtherDates" : form.furtherDates.$modelValue,
					"businessRep" : form.businessRep.$modelValue,
					"legalRep" : form.legalRep.$modelValue,
					"caseStatus" : 0,
					"interimPrayer" : $('#interimPrayer').text(),
					"finalPrayer" : $('#finalPrayer').text(),
					"outcomeLast" : $('#outcomeLast').text(),
					"outcomeNext" : $('#outcomeNext').text(),
					"finImpact" : form.finImpact.$viewValue,
					"caseCategory" : form.caseCategory.$modelValue,
					"subMatter" : $('#subMatter').text(),
					"briefFacts" : $('#briefFacts').text(),
					"enteredBy" : localStorageService.get('userName'),
					"caseID" : vm.caseDetails.caseID,
					"type" : 'edit',
					"userID" : localStorageService.get('payrollNo'),
					"assessYear" : (vm.caseTypeID == 1) ? form.assessYear.$modelValue
							: '',
					"finYear" : (vm.caseTypeID == 1) ? form.finYear.$modelValue
							: '',
					"currency" : vm.curPg,
					"finImp" : vm.finImp,
					"finImpactRecurring" : form.finImpactRecurring.$modelValue,
					"finImpactRecurringDuration" : form.finImpactRecurringDuration.$modelValue,
					"amtOfDisallow" : (vm.form.amtOfDisallow == 0) ? 0
							: parseInt(form.amtOfDisallow.$modelValue),
					"state" : form.state.$modelValue,
					"caseYear" : form.caseYear.$modelValue,
					"courtCaseType" : form.courtCaseType.$modelValue,
					"pvID" : vm.caseDetails.pvID,
					"accountName" : vm.form.accountName,
					"substitutionFiled" : vm.form.substitutionFiled,
					"substitutionAllowed" : vm.form.substitutionAllowed,
					"interimStay" : vm.form.interimStay,
					"stayOrder" : vm.form.stayOrder,

					"sessID" : localStorageService.get('sessionID')
				}
				//alert(JSON.stringify(jsonReq));
				EditCaseService.editCase(jsonReq).then(
						function(result) {

							if (result.data.status == 'T') {
								alert('Case details for id '
										+ vm.caseDetails.caseID
										+ ' edited successfully');
								//$state.go('viewcase');
								$window.close();
								window.opener.location.reload();

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
	}

})();
