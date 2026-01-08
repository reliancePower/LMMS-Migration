(function() {
	'use strict';

	angular.module('regCalApp.ai').controller('AiController',
			AiController);

	AiController.$inject = [ '$state', 'aiService',
			'localStorageService', 'DTOptionsBuilder', 'DTColumnBuilder',
			'DTColumnDefBuilder', '$scope', '$uibModal', '$uibModalStack',
			'$compile', '$window', '$filter', 'dashboardService',
			'AddUserService', 'addCaseService', 'DTInstances' ];

	/* @ngInject */
	function AiController($state, aiService, localStorageService,
			DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $scope,
			$uibModal, $uibModalStack, $compile, $window, $filter,
			dashboardService, AddUserService, addCaseService, DTInstances) {
		var vm = angular.extend(this, {
			goBack : goBack,
			//viewAdditionalNoticeItem : viewAdditionalNoticeItem,
			printCaseDetails : printCaseDetails,
			downloadFile: downloadFile,
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
			vm.caseDetails = localStorageService.get('viewCaseDetails');
			vm.aiDetails = localStorageService.get('viewAiDetails');
			vm.sessID = localStorageService.get('sessionID');

			vm.hearing = vm.aiDetails.aiHistoryArray;


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
		
		function downloadFile(docId) {

					// This should point to a REST endpoint that handles file download
					var downloadUrl = "./rest/V2/downloadPdf?docId=" + encodeURIComponent(docId);

					// Trigger download
					window.open(downloadUrl, "_blank");
				}

		

	}

})();
