(function() {
	'use strict';

	angular.module('regCalApp.adduser').factory('AddUserService',
			AddUserService);

	AddUserService.$inject = [ 'localStorageService', '$http' ];

	/* @ngInject */
	function AddUserService(localStorageService, $http) {
		var service = {
			fetchAllMaster : fetchAllMaster,
			addNewUser : addNewUser,
			fetchBusiVeri : fetchBusiVeri,
			fetchBusiVeriView : fetchBusiVeriView,
			fetchAllMasterUser : fetchAllMasterUser
		};
		return service;

		function fetchAllMaster() {
			var data = {
				"type" : "none",
				"userID" : localStorageService.get('payrollNo'),
				"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : userRegMasterURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/json; charset=utf-8'
				},
				data : data

			});

		}
		function fetchAllMasterUser() {
			var data = {
					"userID" : localStorageService.get('payrollNo'),
					"sessID" : localStorageService.get('sessionID')
			}
			return $http({
				method : 'POST',
				url : userRegMasterURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/json; charset=utf-8'
				},
				data : data
			});

		}

		function addNewUser(data) {

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

		function fetchBusiVeri(data) {

			return $http({
				method : 'POST',
				url : fetchBusiVeriURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}
		function fetchBusiVeriView(data) {

			return $http({
				method : 'POST',
				url : fetchBusiVeriViewURL,
				headers : {
					'Authorization' : authHeader,
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});

		}
	}
})();
