(function() {
	'use strict';

	angular
		.module('regCalApp.addcase')
		.factory('addCaseService', addCaseService);

	addCaseService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function addCaseService(localStorageService, $http) {
		var service = {
			addNewCase: addNewCase,
			addNewMaster : addNewMaster,
			fetchAllMasters : fetchAllMasters,
			fetchMastersForAsset : fetchMastersForAsset

		};
		return service;
		
		function addNewCase(data) {	
			
			return $http({
		            method: 'POST',
		            url: addNewURL,
		            headers: {'Authorization' : authHeader,
		            	'Content-Type': 'application/json; charset=utf-8'
		            },
		            data:data
		        });
							   
			}
		
			function fetchAllMasters(data) {	
			
			return $http({
		            method: 'POST',
		            url: fetchAllMastersURL,
		            headers: {'Authorization' : authHeader,
		            	'Content-Type': 'application/json; charset=utf-8'
		            },
		            data:data
		        });
							   
			}
		
		

		function addNewMaster(data) {	
			
			return $http({
		            method: 'POST',
		            url: addNewMasterURL,
		            headers: {'Authorization' : authHeader,
		            	'Content-Type': 'application/xml; charset=utf-8'
		            },
		            data:data
		        });
							   
			}
		
		function fetchMastersForAsset(data) {	
			
			return $http({
		            method: 'POST',
		            url: fetchMastersForAssetURL,
		            headers: {'Authorization' : authHeader,
		            	'Content-Type': 'application/xml; charset=utf-8'
		            },
		            data:data
		        });
							   
			}
	}
})();
