(function() {
	'use strict';

	angular.module('regCalApp.pvforumupdatelog').controller(
			'PVForumUpdateController', PVForumUpdateController);

	PVForumUpdateController.$inject = [ '$state', 'PVForumUpdateService',
			'DTOptionsBuilder', 'DTColumnBuilder', '$scope',
			'localStorageService', '$compile', '$window', '$filter' ];

	/* @ngInject */
	function PVForumUpdateController($state, PVForumUpdateService,
			DTOptionsBuilder, DTColumnBuilder, $scope, localStorageService,
			$compile, $window, $filter) {
		var vm = angular.extend(this, {
			fetchLog : fetchLog,
			resultArray : [],
			resetFilter : resetFilter,
			startDate : '',
			endDate : '',
			updateNewForum : updateNewForum,
			lastRun : ''
		});
		(function activate() {
			if (localStorageService.get('payrollNo') == undefined
					|| localStorageService.get('payrollNo') == null) {
				$state.go('login');
				return false;
			}
			vm.lastJobRun = localStorageService.get('lastJobRun');
			var date = new Date();
			var day3 = ("0" + date.getDate()).slice(-2);
			var month3 = ("0" + (date.getMonth() + 1)).slice(-2);
			var days3 = (day3) + '/' + (month3) + '/' + date.getFullYear();
			vm.startDate = days3;
			vm.endDate = days3;

			vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType(
					'full_numbers').withOption('order', []).withOption(
					'autoWidth', false);

		})();
		function fetchLog() {
			if (vm.endDate == '' || vm.startDate == '') {
				alert('Start date and End date must be selected');
				return false;
			}

			var stDate = vm.startDate.split('/');
			var convertedSDate = new Date(stDate[2] + '-' + stDate[1] + '-'
					+ stDate[0]);

			var formatted_startDate = $filter('date')(convertedSDate,
					'yyyy-MM-dd')

			var enDate = vm.endDate.split('/');
			var convertedEDate = new Date(enDate[2] + '-' + enDate[1] + '-'
					+ enDate[0]);

			var formatted_endDate = $filter('date')(convertedEDate,
					'yyyy-MM-dd')

			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"endDate" : formatted_endDate,
				"startDate" : formatted_startDate,
				"sessID" : localStorageService.get('sessionID')
			}
			PVForumUpdateService.viewAllLogs(data).then(function(result) {
				vm.resultArray = result.data.resultArray;
				if (vm.resultArray.length == 0)
					alert("No results found");

			}, function caseError(error) {
				alert('There seems some problem. Please try again later...');
				return false;
			});
		}

		function resetFilter() {
			vm.startDate = '';
			vm.endDate = '';
		}

		function updateNewForum() {
			if (confirm('Do you want to get updates of Forum from CIS?')) {
				PVForumUpdateService
						.fetchNewForums()
						.then(
								function(result) {
									console.log(result.data)

								},
								function caseError(error) {
									alert('There seems some problem. Please try again later...');
									return false;
								});
			} else {
				console.log('false');
			}

		}
	}
})();
