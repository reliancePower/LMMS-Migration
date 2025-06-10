(function() {
	'use strict';

	angular
		.module('regCalApp.managenoticedocs')
		.factory('ManageNoticesDocsService', ManageNoticesDocsService);

	ManageNoticesDocsService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function ManageNoticesDocsService(localStorageService, $http) {
		
		var service = {
			fetchDocTypes : fetchDocTypes,
			fetchLmmsDocuments : fetchLmmsDocuments,
			manageDocuments : manageDocuments
		};
		return service;
		
		
		function fetchDocTypes(data){
			return $http({
	            method: 'POST',
	            url: fetchNoticeDocTypeURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function fetchLmmsDocuments(data){
			return $http({
	            method: 'POST',
	            url: fetchNoticeDocsURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function manageDocuments(data){
			return $http({
	            method: 'POST',
	            url: manageNoticeDocsURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		
	}
})();
