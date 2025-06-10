(function() {
	'use strict';

	angular
		.module('regCalApp.managedocs')
		.factory('ManageDocsService', ManageDocsService);

	ManageDocsService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function ManageDocsService(localStorageService, $http) {
		
		var service = {
			fetchDocTypes : fetchDocTypes,
			fetchLmmsDocuments : fetchLmmsDocuments,
			manageDocuments : manageDocuments
		};
		return service;
		
		
		function fetchDocTypes(data){
			return $http({
	            method: 'POST',
	            url: fetchDocTypeURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function fetchLmmsDocuments(data){
			return $http({
	            method: 'POST',
	            url: fetchDocumentsURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function manageDocuments(data){
			return $http({
	            method: 'POST',
	            url: manageDocsURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		
	}
})();
