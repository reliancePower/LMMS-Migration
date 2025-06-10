(function() {
	'use strict';

	angular
		.module('regCalApp.editcase')
		.factory('EditCaseService', EditCaseService);

	EditCaseService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function EditCaseService(localStorageService, $http) {
		var service = {
			getCaseDetails: getCaseDetails,
			editCase : editCase
//			successCacheFn : successCacheFn,
//			errorCacheFn : errorCacheFn
		};
		return service;
		
		
		function getCaseDetails(data){
			return $http({
	            method: 'POST',
	            url: addNewURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function editCase(data){
			return $http({
	            method: 'POST',
	            url: editCaseURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
	}
})();
