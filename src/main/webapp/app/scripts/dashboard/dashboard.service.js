(function() {
	'use strict';

	angular.module('regCalApp.dashboard').factory('dashboardService',
			dashboardService);

	dashboardService.$inject = [ 'localStorageService', '$http' ];

	/* @ngInject */
	function dashboardService(localStorageService, $http) {
		var service = {
			fetchAllMaster : fetchAllMaster,
			fetchCompanyMaster : fetchCompanyMaster,
			fetchVerticalMaster : fetchVerticalMaster,
			getHomePageInfo : getHomePageInfo,
			fetchRefNo : fetchRefNo,
			getSummaryUpcoming : getSummaryUpcoming,
			manageMaster : manageMaster,
			searchAlert : searchAlert
		};
		return service;

		function searchAlert(data) {

			return $http({
				method : 'POST',
				url : searchAlertURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/json; charset=utf-8'
				},
				data : data
			});

		}

		function manageMaster(data) {

			return $http({
				method : 'POST',
				url : manageAlertURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/json; charset=utf-8'
				},
				data : data
			});

		}

		function fetchAllMaster() {
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : fetchAllCompanyURL,
				headers : {
					//'Authorization' : authHeader,
					'Authorization' : localStorageService.get('authHeader'),
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}

		function fetchAllMaster() {
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : fetchAllCompanyURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}

		function getSummaryUpcoming(data) {

			return $http({
				method : 'POST',
				url : getSummaryUpcomingURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}

		function fetchRefNo(item) {
			var data = {
				"id" : item,
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : fetchRefNoURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/json; charset=utf-8'
				},
				data : data
			});

		}

		function getHomePageInfo() {
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : homePageURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}

		function fetchCompanyMaster(item) {
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"companyID" : item,
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : fetchAllBusinessURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}
		function fetchVerticalMaster(item) {
			var data = {
				"userID" : localStorageService.get('payrollNo'),
				"businessID" : item,
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : fetchAllVerticalURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}

	}
})();
