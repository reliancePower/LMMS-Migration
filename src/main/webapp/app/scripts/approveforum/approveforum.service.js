(function() {
	'use strict';

	angular
		.module('regCalApp.approveforum')
		.factory('ApproveForumService', ApproveForumService);

	ApproveForumService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function ApproveForumService(localStorageService, $http) {
		var service = {
			fetchApproveReq: fetchApproveReq
		};
		return service;

		
		function fetchApproveReq(data){
			
			return $http({
	            method: 'POST',
	            url: deviceRequestURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
						   
		
		}
		
		
						   
		
		
		
	}
})();
