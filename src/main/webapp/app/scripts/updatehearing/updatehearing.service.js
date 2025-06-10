(function() {
	'use strict';

	angular
		.module('regCalApp.updatehearing')
		.factory('updatehearingService', updatehearingService);

	updatehearingService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function updatehearingService(localStorageService, $http) {
		
		var service = {
			getUpdateHearing: getUpdateHearing,
			fetchMaster : fetchMaster,
			addNewMaster : addNewMaster,
            updateHearing : updateHearing,
            fetchHearingHistory : fetchHearingHistory

		};
		return service;
		
	function getUpdateHearing(){
			var data = {"userID" : localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID')}
			return $http({
	            method: 'POST',
	            url: hearingUpdateSummaryURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
						   
		
        }
        
        function fetchHearingHistory(data){
            return $http({
	            method: 'POST',
	            url: hearingHistoryURL,
	            headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
					
        }

		function fetchMaster(){
			var data = {"userID" : localStorageService.get('payrollNo'), 'type' : 'fetch', 
					"sessID" : localStorageService.get('sessionID')};
			
			return $http({
	            method: 'POST',
	            url: filterCasesURL,
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
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
		        });
							   
			}

			function updateHearing(data){
			return $http({
	            method: 'POST',
	            url: filterCasesURL,
	           headers: {'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}
		
		
	}
})();
