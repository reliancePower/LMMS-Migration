(function() {
	'use strict';

	angular.module('regCalApp.addcase').controller('AddCaseController',
			AddCaseController);

	AddCaseController.$inject = [ '$state', 'addCaseService', '$scope',
			'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
			'$uibModal', '$uibModalStack', 'dashboardService', '$sce',
			'$window', 'ManageForumService' , 'navbarService'];

	/* @ngInject */
	function AddCaseController($state, addCaseService, $scope,
			localStorageService, DTOptionsBuilder, DTColumnBuilder, $uibModal,
			$uibModalStack, dashboardService, $sce, $window, ManageForumService, navbarService) {
		var vm = angular.extend(this, {
			submit : submit,
			fetchAllMaster : fetchAllMaster,
			fetchCompanyMaster : fetchCompanyMaster,
			fetchVerticalMaster : fetchVerticalMaster,
			openBenchModal : openBenchModal,
			openAorCompModal : openAorCompModal,
			openCounselCompModal : openCounselCompModal,
			openBusinessRepModal : openBusinessRepModal,
			openLegalRepModal : openLegalRepModal,
			showMultiplePicker : showMultiplePicker,
			openSubjectModal : openSubjectModal,
			openFactsModal : openFactsModal,
			interimPModal : interimPModal,
			finalPModal : finalPModal,
			outcomeLastModal : outcomeLastModal,
			outcomeNextModal : outcomeNextModal,
			AddMember : AddMember,
			moveMember : moveMember,
			openPetitionerModal : openPetitionerModal,
			openRespondentModal : openRespondentModal,
			removeMember : removeMember,
			diplayMember : diplayMember,
			benchSelected : [],
			aorCompanySelected : [],
			counselCompanySelected : [],
			businessRepSelected : [],
			legalRepSelected : [],
			selComp : '',

			factsHTML : '',
			intrimPHTML : '',
			finalPHTML : '',
			outcomeLastHTML : '',
			outcomeNextHTML : '',
			selBusiness : '',
			selBench : '',
			selAorCompany : '',
			selCounselCompany : '',
			businessRep : '',
			legalRep : '',
			fetchRefNo : fetchRefNo,
			form : {
				company : '',
				business : '',
				vertical : '',
				caseType : '',
				caseYear : 0,
				courtCaseType : '',
				forumCategory : '',
				forum : '',
				caseNo : '',
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
				caseStatus : 0,
				subMatter : '',
				briefFacts : '',
				interimPrayer : '',
				finalPrayer : '',
				outcomeLast : '',
				outcomeNext : '',
				finImpact : '',
				caseCategory : 0,
				subMatter1 : '',
				refNo : '',
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
			companyInfo : [],
			businessInfo : [],
			verticalInfo : [],
			forumInfo : [],
			benchInfo : [],
			stateInfo : [],
			businessRepInfo : [],
			legalRepInfo : [],
			caseStatusInfo : [],
			caseCategoryInfo : [],
			aorCompanyInfo : [],
			counselCompanyInfo : [],
			subMatterHTML : '',
			froalaOptions : {
				height : 250,
				toolbarButtons : [ 'bold', 'italic', 'underline',
						'strikeThrough', 'subscript', 'superscript', 'outdent',
						'indent', 'clearFormatting', 'insertTable', 'html',
						'|', 'fontFamily', 'fontSize', 'color', 'inlineStyle',
						'paragraphStyle', '|', 'paragraphFormat', 'align',
						'formatOL', 'formatUL', 'outdent', 'indent' ]
			},
			selPetitioner : '',
			petitionerSelected : [],
			selRespondent : '',
			respondentSelected : [],
			years : [],
			checkYear : checkYear,
			yearSel : false,
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
			fetchAssessYear : fetchAssessYear,
			forumCategoryInfo : [],
			caseYearInfo : [],
			fetchForumInfo : fetchForumInfo,
			fetchCaseTypeInfo : fetchCaseTypeInfo,
			fetchCaseTypeArray : [],

			assetSegmantInfo : [],
			trustNameInfo : [],
			accountNameInfo : [],
			fetchTrustName : fetchTrustName,
			fetchAccountName : fetchAccountName,
			resultArray : [],
			resultArrayInterim : [],
			sendReqNewForum : sendReqNewForum,
			inputFormatter : inputFormatter,
			caseNoOnChange : caseNoOnChange

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			fetchAllMaster();
			localStorageService.remove('searchCaseDetails');
			var d = new Date();
			var n = d.getFullYear();
			var next = n + 1;

			for (var i = 1990; i <= next; i++) {
				var t = i + 1;
				vm.years.push(i + '-' + t);
			}
			vm.resultArray = [ 'Yes', 'No', 'NA' ]
			vm.resultArrayInterim = [ 'Yes', 'No' ]
			for (var i = n; i >= 1980; i--) {
				vm.caseYearInfo.push(i);
			}

			if (localStorageService.get("itemPerPage") === null) {
				localStorageService.set("itemPerPage", 10);
			}

			vm.filteredlist = vm.finImp;
			vm.numPerPage = getPerPage();
			vm.curPg = 'INR'
			//$('#numPerPage').val(vm.numPerPage);

		})();

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

		function sendReqNewForum() {
			vm.form.forumCategory = 'others';
			vm.form.forum = '';
			var jsonReq = {
				"type" : "fetchForum",
				"category" : vm.form.forumCategory,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			ManageForumService.manageForum(jsonReq).then(function(result) {
				console.log(result.data);
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.forumInfo = result.data.resultArray;
				vm.form.forum = 999999;
				vm.fetchCaseTypeArray.push({'id':1000000, 'name' : 'Others'});
				vm.form.courtCaseType = 1000000;
				$state.goToNewTab('manageforum', {});
				
				
			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
			// fetchForumInfo(vm.form.forumCategory)

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

		function fetchTrustName(item) {

			var jsonReq = {
				"type" : "fetchTrustName",
				"assetSegment" : item,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			//alert(JSON.stringify(jsonReq))
			addCaseService.fetchMastersForAsset(jsonReq).then(function(result) {
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
				vm.accountNameInfo = result.data.accountNameArray;

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});
		}

		function addExpense() {

			if (vm.curExpense.id) {
				vm.newexpense.principal = parseInt(vm.newexpense.principal)
				vm.newexpense.penalty = parseInt(vm.newexpense.penalty)
				vm.newexpense.interest = parseInt(vm.newexpense.interest)
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
		function checkYear() {
			var sel = $('#caseType').val();
			if (parseInt(sel) == 1) {
				$('#fieldSetId').css('display', 'block');
				$('#caseTypeCont').css('display', 'flex');
				vm.yearSel = true;
			} else if (vm.form.company == 3 && vm.form.business == 18
					&& vm.form.vertical == 99) {
				$('#fieldSetId').css('display', 'block');
				$('#caseTypeCont').css('display', 'none');
				vm.yearSel = false;
			} else {
				$('#fieldSetId').css('display', 'none');
				$('#caseTypeCont').css('display', 'none');
				vm.yearSel = false;
			}

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
				console.log(result.data);
				vm.forumInfo = result.data.resultArray;

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
				console.log(result.data);
				vm.fetchCaseTypeArray = result.data.resultArray;
				vm.form.caseNo = '';

			}, function loginError(error) {

				alert('Something went wrong... Pls try again...');
				return false;
			});

		}

		function caseNoOnChange() {
			if (vm.form.forum.id >= 100000) {
				$('#caseNo').removeAttr('num-only')
				$('#caseNo').removeAttr('min')
			} else {
				vm.form.caseNo = vm.form.caseNo.replace(/[^\d]/g, '');
				$('#caseNo').attr('num-only', '')
				$('#caseNo').attr('min', '0')
			}
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

		function fetchAllMaster() {

			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"type" : "fetchMasterForAdd",
				"sessID" : localStorageService.get('sessionID')
			}
			addCaseService.fetchAllMasters(jsonReq).then(function(result) {
				if(result.data.status == 'S'){
					alert(result.data.msg);
					
						$window.localStorage.clear();
						localStorageService.clearAll;
						localStorageService.cookie.clearAll;
						$state.go('login');
					} 
				
				vm.companyInfo = result.data.companyMaster;
				vm.benchInfo = result.data.benchMaster;
				vm.forumCategoryInfo = result.data.forumCategoryMaster;
				vm.aorCompanyInfo = result.data.aorCompanyMaster;
				vm.counselCompanyInfo = result.data.counselCompanyMaster;
				vm.aorRespInfo = result.data.aorCompanyMaster;
				vm.counselRespInfo = result.data.counselCompanyMaster;
				vm.caseStatusInfo = result.data.statusMaster;
				vm.caseCategoryInfo = result.data.categoryMaster;
				vm.caseTypeInfo = result.data.caseTypeMaster;
				vm.stateInfo = result.data.stateArray;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function showMultiplePicker() {
			var myEl = angular.element(document.querySelector('#furtherDates'));
			myEl.attr('multiple-date-picker', "");
		}
		function fetchCompanyMaster(item) {
			vm.selComp = item;
			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"compID" : vm.selComp.toString(),
				"type" : "fetchBusiness",
				"sessID" : localStorageService.get('sessionID')
			}
			addCaseService.fetchAllMasters(jsonReq).then(function(result) {
				vm.businessInfo = result.data.businessMaster;
				vm.businessRepInfo = result.data.businessRepMaster;
				vm.legalRepInfo = result.data.legalRepMaster;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}
		function fetchRefNo(item) {

			dashboardService.fetchRefNo(item).then(function(result) {
				vm.form.refNo = result.data.refNo;
			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

			if (vm.form.company == 3 && vm.form.business == 18
					&& vm.form.vertical == 99) {
				$('#fieldSetId').css('display', 'block');
				var jsonReq = {
					"type" : "fetchAssetSegment",
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
				}
				//alert(JSON.stringify(jsonReq))
				addCaseService.fetchMastersForAsset(jsonReq).then(
						function(result) {
							vm.assetSegmantInfo = result.data.assetNameArray;

						}, function loginError(error) {

							alert('Something went wrong... Pls try again...');
							return false;
						});
			}
		}

		function fetchVerticalMaster(item) {
			vm.selBusiness = item;
			dashboardService.fetchVerticalMaster(item).then(function(result) {
				vm.verticalInfo = result.data.verticalMaster;
			}, function caseError(error) {
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
					"company" : form.company.$modelValue,
					"business" : form.business.$modelValue,
					"vertical" : parseInt(form.vertical.$modelValue),
					"caseType" : parseInt(form.caseType.$modelValue),
					"forum" : form.forum.$modelValue.id,
					"caseNo" : form.caseNo.$modelValue,
					"petitioner" : vm.petitionerHTML,
					"respondent" : vm.respondentHTML,
					"bench" : form.bench.$modelValue,
					"aorOfCompany" : form.aorOfCompany.$modelValue,
					"counselOfCompany" : form.counselOfCompany.$modelValue,
					"counselOfRespondent" : form.counselOfRespondent.$modelValue,
					"aorOfRespondent" : form.aorOfRespondent.$modelValue,
					"lastDateOfHearing" : form.lastDateOfHearing.$modelValue,
					"nextDateOfHearing" : form.nextDateOfHearing.$modelValue,
					"furtherDates" : form.furtherDates.$modelValue,
					"businessRep" : form.businessRep.$modelValue,
					"legalRep" : form.legalRep.$modelValue,
					"caseStatus" : form.caseStatus.$modelValue,
					"interimPrayer" : $('#interimPrayer').text(),
					"finalPrayer" : $('#finalPrayer').text(),
					"outcomeLast" : $('#outcomeLast').text(),
					"outcomeNext" : $('#outcomeNext').text(),
					"finImpact" : form.finImpact.$modelValue,
					"caseCategory" : form.caseCategory.$modelValue,
					"subMatter" : $('#subMatter').text(),
					"briefFacts" : $('#briefFacts').text(),
					"enteredBy" : localStorageService.get('userName'),
					"type" : 'add',
					"userID" : localStorageService.get('payrollNo'),
					"assessYear" : (vm.yearSel == true) ? form.assessYear.$modelValue
							: '',
					"finYear" : (vm.yearSel == true) ? form.finYear.$modelValue
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
					"accountName" : vm.form.accountName,
					"substitutionFiled" : vm.form.substitutionFiled,
					"substitutionAllowed" : vm.form.substitutionAllowed,
					"interimStay" : vm.form.interimStay,
					"stayOrder" : vm.form.stayOrder,
					"sessID" : localStorageService.get('sessionID')
				}
				//alert(JSON.stringify(jsonReq))
				addCaseService
						.addNewCase(jsonReq)
						.then(
								function(result) {

									if (result.data.status == 'T') {
										alert('Case details added successfully with case ID - '
												+ result.data.caseID
												+ ' and Reference No - '
												+ result.data.refNo);
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
				return;

		}

		function financialImpactModal() {
			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'finImpact.html',
				backdrop : 'static',
				keyboard : false,
				size : 'lg',

				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});

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

			vm.subMatterHTML = $('#subMatter').html();

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
			vm.factsHTML = $('#briefFacts').html();
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
			vm.intrimPHTML = $('#interimPrayer').html();
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
			vm.finalPHTML = $('#finalPrayer').html();
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
			vm.outcomeLastHTML = $('#outcomeLast').html();
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
			vm.outcomeNextHTML = $('#outcomeNext').html();
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
					html += '<li>P' + ind + '.' + name + '</li>';
				});
				vm.petitionerHTML = '<ol class="myUL" >' + html + '</ol>';
			} else if (modalType == 'respondent') {
				var html1 = '';
				var ind = 0;
				_.each(vm.respondentSelected, function(name, index) {
					ind = index + 1;
					html1 += '<li>R' + ind + '.' + name + '</li>';
				});
				vm.respondentHTML = '<ol class="myUL" >' + html1 + '</ol>';
			}

			$uibModalStack.dismissAll();
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
	}

})();
