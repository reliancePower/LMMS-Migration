(function() {
	'use strict';

	angular.module('regCalApp.approvedevice').controller(
			'ApproveDeviceController', ApproveDeviceController);

	ApproveDeviceController.$inject = [ '$state', 'ApproveDeviceService',
			'DTOptionsBuilder', 'DTColumnBuilder', '$scope',
			'localStorageService', '$compile', '$window' ];

	/* @ngInject */
	function ApproveDeviceController($state, ApproveDeviceService,
			DTOptionsBuilder, DTColumnBuilder, $scope, localStorageService,
			$compile, $window) {
		var vm = angular.extend(this, {
			dtOptions : {},
			today : new Date(),
			approveRequestArray : [],
			dtColumnDefs : [],
			approveUserReq : approveUserReq,
			rejectUserReq : rejectUserReq

		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			getPageInfo();
			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'responsive', true);

		})();

		function getPageInfo() {
			var data = {
				"type" : 'fetch',
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			};

			ApproveDeviceService.fetchApproveReq(data).then(function(result) {
				if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
				vm.approveRequestArray = result.data.userArrayMaster;

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});

		}

		function approveUserReq(item) {
			var data = {
				"type" : 'approve',
				"id" : item.id,
				"company" : item.company,
				"emailID" : item.emailID,
				"userID" : item.userID,
				"deviceNo" : item.deviceNo,
				"mobileNo" : item.mobileNo,
				"name" : item.name,
				"deviceUID" : item.deviceUID,
				"approveUserID" : localStorageService.get('payrollNo'),
				"deviceType" : item.deviceType,
				"deviceManufact" : item.deviceManufact,
				"deviceVersion" : item.deviceVersion,
				"uID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
				
			}

			if (confirm('Are you sure to approve ' + item.name
					+ ' device request')) {
				ApproveDeviceService
						.fetchApproveReq(data)
						.then(
								function(result) {
									if (result.data.status == 'T') {
										alert('Device approved successfully');
										$state.reload();
										//$window.location.reload();
									} else {
										alert('Not able to approve device... Please try again later');
										return false;
									}

								},
								function caseError(error) {
									alert('There seems some problem. Please try again later...');
									return false;
								});

			} else {
				console.log('false');
				return false;
			}

		}

		function rejectUserReq(item) {
			var data = {
				"type" : 'reject',
				"id" : item.id,
				"company" : item.company,
				"emailID" : item.emailID,
				"userID" : item.userID,
				"deviceNo" : item.deviceNo,
				"mobileNo" : item.mobileNo,
				"name" : item.name,
				"deviceUID" : item.deviceUID,
				"approveUserID" : localStorageService.get('payrollNo'),
				"deviceType" : item.deviceType,
				"deviceManufact" : item.deviceManufact,
				"deviceVersion" : item.deviceVersion,
				"uID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			if (confirm('Are you sure to reject ' + item.name
					+ ' device request')) {
				ApproveDeviceService
						.fetchApproveReq(data)
						.then(
								function(result) {
									if (result.data.status == 'T') {
										alert('Device rejected successfully');
										$state.reload();
										//$window.location.reload();
									} else {
										alert('Not able to reject device... Please try again later');
										return false;
									}

								},
								function caseError(error) {
									alert('There seems some problem. Please try again later...');
									return false;
								});

			} else {
				console.log('false');
				return false;
			}

		}

	}
})();
