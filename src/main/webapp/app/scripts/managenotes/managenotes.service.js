(function() {
	'use strict';

	angular
		.module('regCalApp.managenotes')
		.factory('ManageNotesService', ManageNotesService);

	ManageNotesService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function ManageNotesService(localStorageService, $http) {
		
		var service = {
				manageNotes : manageNotes,
				fetchNotes : fetchNotes,
			manageDocuments : manageDocuments
		};
		return service;
		
		
		function manageNotes(data){
			return $http({
	            method: 'POST',
	            url: manageNotesURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function fetchNotes(data){
			return $http({
	            method: 'POST',
	            url: manageNotesURL,
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
