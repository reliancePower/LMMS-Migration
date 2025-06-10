(function() {
	'use strict';

	angular.module('regCalApp')
			.controller('NavBarController', NavBarController);

	NavBarController.$inject = [ '$state', '$scope', 'localStorageService',
			'navbarService', '$window', '$uibModal', '$uibModalStack',
			'$filter', '$rootScope', 'Idle', 'Keepalive' ];

	/* @ngInject */
	function NavBarController($state, $scope, localStorageService,
			navbarService, $window, $uibModal, $uibModalStack, $filter,
			$rootScope, Idle, Keepalive) {
		var vm = angular.extend(this, {
			$state : $state,
			logout : logout,
			openUserModal : openUserModal,
			disabled : 0,
			updateDOB : updateDOB

		});

		(function activate() {
			vm.lastLogin = localStorageService.get('lastLogin');
			vm.accessCtrl = localStorageService.get('accessCtrl');
			vm.userName = localStorageService.get('userName');
			vm.userType = localStorageService.get('userType');
			vm.mobileNo = localStorageService.get('mobileNo');
			vm.payroll = localStorageService.get('payrollNo');
			vm.company = localStorageService.get('company');
			vm.caseCategory = localStorageService.get('caseCategory');
			vm.emailID = localStorageService.get('emailID');
			vm.imeiNo1 = localStorageService.get('deviceText1');
			vm.imeiNo2 = localStorageService.get('deviceText2');
			vm.imeiNo3 = localStorageService.get('deviceText3');
			vm.maxUsageID = localStorageService.get('maxUsageID');

			vm.dobS = localStorageService.get('dob');
			vm.dobS = vm.dobS.substring(4, 8) + '-' + vm.dobS.substring(2, 4)
					+ '-' + vm.dobS.substring(0, 2);

			vm.dob = new Date(vm.dobS);

			vm.dobA = $filter('date')(vm.dob, "dd/MM/yyyy");
			vm.dobS = localStorageService.get('dob');
			if (localStorageService.get('dob') == '01012018') {
				if ($state.current.name === 'dashboard') {// toState variable
															// see the state
															// you're going

					openUserModal();
				}

			}

		})();

		function updateDOB() {

			var dobA = vm.dobA.split('/').join('');

			var jsonReq = {
				"userID" : localStorageService.get('payrollNo'),
				"dob" : dobA,
				"sessID" : localStorageService.get('sessionID')
			}
			navbarService
					.updateDOB(jsonReq)
					.then(
							function(result) {

								if (result.data.status == 'T') {

									if (localStorageService.get('dob') != dobA) {
										alert('You have successfully changed the DOB. Now you will be redirected to login page');

										$window.localStorage.clear();
										localStorageService.clearAll;
										localStorageService.cookie.clearAll;
										$state.go('login');
									} else {
										alert('You have changed the DOB successfully...');

										$uibModalStack.dismissAll();
									}

								} else {
									alert(result.data.msg);
									return false;
								}
							},
							function loginError(error) {

								alert('Something went wrong... Pls try again...');
								return false;
							});
		}

		function openUserModal() {
			vm.dobS = localStorageService.get('dob');

			vm.dobS = vm.dobS.substring(4, 8) + '-' + vm.dobS.substring(2, 4)
					+ '-' + vm.dobS.substring(0, 2);

			vm.dob = new Date(vm.dobS);
			vm.dobA = $filter('date')(vm.dob, "dd/MM/yyyy");

			$(document).ready(function() {

				$('#dobN').datepicker({
					format : 'dd/mm/yyyy',
					endDate : new Date(),
					autoclose : true,
					orientation : "top"
				});

			});

			var modalInstance = $uibModal.open({
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'userModalTemp.html',
				size : 'lg',
				scope : $scope
			});

			modalInstance.result.then(function(response) {
			}, function() {
				vm.disabled = 0;
				console.log('Modal dismissed at: ' + new Date());
			});
		}

		function logout() {
			if (confirm('Are you sure to logout LMMS app')) {
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
			} else
				return false;
		}

		function closeModals() {
			if ($scope.warning) {
				$scope.warning.close();
				$scope.warning = null;
				logout();
			}

			if ($scope.timedout) {
				$scope.timedout.close();
				$scope.timedout = null;
				logout();
			}
		}

		$scope.$on('IdleStart', function() {
			closeModals();

			$scope.warning = $uibModal.open({
				templateUrl : 'warning-dialog.html',
				windowClass : 'modal-danger'
			});
		});

		$scope.$on('IdleEnd', function() {
			closeModals();
		});

		$scope.$on('IdleTimeout', function() {
			closeModals();

		});

	}

})();
