(function() {
	'use strict';

	angular
		.module('regCalApp.login')
		.factory('loginService', loginService);

	loginService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function loginService(localStorageService, $http) {
		var service = {
			login: login,
			getUser : getUser,
		};
		return service;
	
		function getUser() {
			return localStorageService.get('webmailID');
		}

		function login(data) {	
			
			return $http({
		            method: 'POST',
		            url: loginURL,
		            headers: {
		            	'Authorization' : authHeader,
		            	'Content-Type': 'application/xml; charset=utf-8'
		            },
		            data:data
		        });
							   
			}
		}
		
	
})();
