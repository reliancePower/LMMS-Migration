(function() {
	'use strict';

	angular
		.module('regCalApp.viewnotice')
		.factory('ViewNoticeService', ViewNoticeService);

	ViewNoticeService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function ViewNoticeService(localStorageService, $http) {

		var service = {
				fetchMaster : fetchMaster,
				fetchAllNotices : fetchAllNotices,
				fetchNoticeInfo : fetchNoticeInfo,
				searchNotices : searchNotices
		};
		return service;
		
		function fetchNoticeInfo(item){
			var data = {"enteredBy" : localStorageService.get('payrollNo'), "type" : "fetchNoticeInfo", "sessID" : localStorageService.get('sessionID'), "noticeID" : item}
			
			return $http({
		            method: 'POST',
		            url: noticeManageURL,
		            headers: {		            	
		            	'Authorization' : authHeader,
		            	'Content-Type': 'application/json; charset=utf-8'
		            },
		            data:data
		        });
		}
		
		function fetchAllNotices(){
			var data = {"enteredBy" : localStorageService.get('payrollNo'), "type" : "fetchNotices", "sessID" : localStorageService.get('sessionID')}
		
		return $http({
	            method: 'POST',
	            url: noticeManageURL,
	            headers: {		            	
	            		'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
					
		}
		function fetchMaster(){
			var data = {"userID" : localStorageService.get('payrollNo'), 'type' : 'fetchMaster', 
					"sessID" : localStorageService.get('sessionID')};
			
			return $http({
	            method: 'POST',
	            url: noticeManageURL,
	            headers: {
	            	'Authorization' : authHeader,
	            	//'Authorization' : localStorageService.get('authHeader'),
	            	'Content-Type': 'application/xml; charset=utf-8'
	            },
	            data:data
	        });
		}
	
		function searchNotices(data){
			return $http({
	            method: 'POST',
	            url: searchNoticeURL,
	            headers: {		            	
	            		'Authorization' : authHeader,
	            	'Content-Type': 'application/json; charset=utf-8'
	            },
	            data:data
	        });
		}						
		
	}
})();
