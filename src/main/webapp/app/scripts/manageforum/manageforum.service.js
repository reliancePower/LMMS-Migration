(function() {
	'use strict';

	angular
		.module('regCalApp.manageforum')
		.factory('ManageForumService', ManageForumService);

	ManageForumService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function ManageForumService(localStorageService, $http) {
		
		var service = {
				manageForum : manageForum
		};
		return service;
		
		
		function manageForum(data){
			return $http({
	            method: 'POST',
	            url: manageForumURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		
		
		
	}
})();
