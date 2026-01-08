(function() {
	'use strict';

	angular
		.module('regCalApp.searchcase')
		.factory('SearchCaseService', SearchCaseService);

	SearchCaseService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function SearchCaseService(localStorageService, $http) {

		var viewDetails = '';
		var service = {
			viewAllCases : viewAllCases,
			disableCase : disableCase,
			viewCase : viewCase,
			viewAiSuggestions : viewAiSuggestions,
			getViewCaseDetails : getViewCaseDetails,
			setViewCaseDetails : setViewCaseDetails,
			fetchMaster : fetchMaster
            
		};
		return service;
		
		  
        
		function fetchMaster(){
			var data = {"userID" : localStorageService.get('payrollNo'), 'type' : 'fetch', "sessID" : localStorageService.get('sessionID')};
			
			return $http({
	            method: 'POST',
	            url: filterCasesURL,
	            headers: {
	            	'Authorization' : authHeader,
	            	//'Authorization' : localStorageService.get('authHeader'),
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
	
		function getViewCaseDetails(){
			return viewDetails; 
		}
		function setViewCaseDetails(item){
			viewDetails = item; 
			localStorageService.set('viewDetails', viewDetails)
		}
		
		function viewAllCases(data){
			
			
			return $http({
	            method: 'POST',
	            url: viewAllCasesURL,
	            headers: {
	            	'Authorization' : authHeader,
	            	//'Authorization' : localStorageService.get('authHeader'),
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function disableCase(data){
			return $http({
	            method: 'POST',
	            url: editCaseURL,
	            headers: {
	            	'Authorization' : authHeader,
	            	//'Authorization' : localStorageService.get('authHeader'),
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function viewCase(item){
			var data = {"caseID" : parseInt(item), "sessID" : localStorageService.get('sessionID'), "userID" : localStorageService.get('payrollNo')};
			return $http({
	            method: 'POST',
	            url: viewCaseURL,
	            headers: {
	            	'Authorization' : authHeader,
	            	//'Authorization' : localStorageService.get('authHeader'),
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		function viewAiSuggestions(item){
					var data = {"caseID" : parseInt(item), "sessID" : localStorageService.get('sessionID'), "userID" : localStorageService.get('payrollNo')};
					return $http({
			            method: 'POST',
			            url: viewAiURL,
			            headers: {
			            	'Authorization' : authHeader,
			            	//'Authorization' : localStorageService.get('authHeader'),
			            	'Content-Type': 'application/xml; charset=utf-8'
			            },
			            data:data
			        });
				}
		
		
	}
})();
