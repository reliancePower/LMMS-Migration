(function() {
	'use strict';

	angular
		.module('regCalApp')
		.factory('navbarService', navbarService);

	navbarService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function navbarService(localStorageService, $http) {
		var service = {
			logout: logout,
			updateDOB : updateDOB

		};
		return service;
		
		
		function logout(){
			var data = {"userID" : localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID'), 
					"maxUsageID" :localStorageService.get('maxUsageID')}
			return $http({
	            method: 'POST',
	            url: logoutURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function updateDOB(data){
			return $http({
	            method: 'POST',
	            url: updateDOBURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		
	}
})();
