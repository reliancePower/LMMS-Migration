(function() {
	'use strict';

	angular.module('regCalApp.viewcase').controller('ViewCaseController',
			ViewCaseController);

	ViewCaseController.$inject = [ '$state', 'viewCaseService',
			'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
			'DTColumnDefBuilder', '$scope', '$uibModal', '$uibModalStack',
			'$compile', '$window', '$filter', 'dashboardService',
			'AddUserService', 'addCaseService', 'DTInstances' ];

	/* @ngInject */
	function ViewCaseController($state, viewCaseService, localStorageService,
			DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $scope,
			$uibModal, $uibModalStack, $compile, $window, $filter,
			dashboardService, AddUserService, addCaseService, DTInstances) {
		var vm = angular.extend(this, {
			goBack : goBack,
			printCaseDetails : printCaseDetails,
			today : new Date(),			
			dtColumnDefs : [],					
			hearing : [],
			finImpArray : [],
			currency : '',
            grandTotal : 0,
            userName : '',
            showLockModal : showLockModal,
			lockInfo : ''

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
			vm.userType = localStorageService.get('userType');
			vm.caseDetails = localStorageService.get('viewCaseDetails')
			vm.sessID = localStorageService.get('sessionID');
			vm.caseID = vm.caseDetails.caseID;
			vm.company = vm.caseDetails.company;
			vm.business = vm.caseDetails.business;
			vm.vertical = vm.caseDetails.vertical;
			vm.caseType = vm.caseDetails.caseType;
			vm.forum = vm.caseDetails.forum;
			vm.caseNo = vm.caseDetails.caseNo;
			vm.petitioner = vm.caseDetails.petitioner;
			vm.respondent = vm.caseDetails.respondent;
			vm.bench = vm.caseDetails.bench;
			vm.aorOfCompany = vm.caseDetails.aorOfCompany;
			vm.counselOfCompany = vm.caseDetails.counselOfCompany;
			vm.aorOfRespondent = vm.caseDetails.aorOfRespondent;
			vm.counselOfRespondent = vm.caseDetails.counselOfRespondent;
			vm.lastHearingDate = vm.caseDetails.lastHearingDate;
			vm.nextHearingDate = vm.caseDetails.nextHearingDate;
			vm.businessRep = vm.caseDetails.businessRep;
			vm.legalRep = vm.caseDetails.legalRep;
			vm.subMatter = vm.caseDetails.subMatter;
			vm.briefFacts = vm.caseDetails.briefFacts;
			vm.interimPrayer = vm.caseDetails.interimPrayer;
			vm.finalPrayer = vm.caseDetails.finalPrayer;
			vm.outcomeLast = vm.caseDetails.outcomeLast;
			vm.outcomeNext = vm.caseDetails.outcomeNext;
			vm.finImpact = vm.caseDetails.finImpact;
			vm.enteredBy = vm.caseDetails.enteredBy;
			vm.refNo = vm.caseDetails.refNo;
			vm.caseStatus = vm.caseDetails.statusName;
			vm.caseCategory = vm.caseDetails.caseCategoryName;
			vm.assessYear = vm.caseDetails.assessYear;
			vm.finYear = vm.caseDetails.finYear;
			vm.amtOfDisallow = vm.caseDetails.amtOfDisallow;
			vm.state = vm.caseDetails.state;
			vm.caseTypeID = vm.caseDetails.caseTypeID;
			vm.caseYear = vm.caseDetails.caseYear;
			vm.caseTypeName = vm.caseDetails.caseTypeName;
			
			// New code
			vm.hearing = vm.caseDetails.hearingArray;
			vm.finImpArray = vm.caseDetails.finImpArray;
			vm.paperArray = vm.caseDetails.paperArray;

			if (vm.finImpArray.length > 0) {

				vm.currency = vm.caseDetails.finImpArray[0].currency;
				_.each(vm.finImpArray, function(item, index) {
					vm.grandTotal += parseInt(item.total);
				})

			}

		})();

		function goBack() {
			$window.close();

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

		function printCaseDetails() {

			var title = '<img  style = "width: 200px;height: auto;padding: 16px 15px 0px 10px;" src = "app/img/logo.png" /><h5 style = "display: inline-block;padding-left: 50px;color : #005199 !important;font-weight:600;">'
					+ $('#textTitle').text() + '</h5><hr>';
			var w = window.open();
			w.document.write($("#print-header").html() + title
					+ $("#viewAllMatter").html() + $("#print-footer").html());
			w.document.close();
			w.focus();
		}

		

	}

})();
