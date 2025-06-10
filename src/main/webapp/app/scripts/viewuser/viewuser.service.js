(function() {
	'use strict';

	angular.module('regCalApp.viewuser').factory('viewUserService',
			viewUserService);

	viewUserService.$inject = [ 'localStorageService', '$http' ];

	/* @ngInject */
	function viewUserService(localStorageService, $http) {

		var service = {
			getAllUsers : getAllUsers,
			fetchUserDetails : fetchUserDetails,
			disableUserItem : disableUserItem
		};
		return service;

		function fetchUserDetails(userID) {
			var data = {
				"payrollNo" : userID,
				'type' : 'fetch',
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			};

			return $http({
				method : 'POST',
				url : userManageURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});
		}
		function disableUserItem(data) {

			return $http({
				method : 'POST',
				url : userManageURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});
		}

		function getAllUsers(data) {

			return $http({
				method : 'POST',
				url : userManageURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});
		}
	}
})();
