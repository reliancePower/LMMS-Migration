(function() {
	'use strict';

	angular
		.module('regCalApp.gst')
		.factory('GstService', GstService);

	GstService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function GstService(localStorageService, $http) {

		var service = {
				fetchMaster : fetchMaster,
				fetchAllNotices : fetchAllNotices,
				fetchNoticeInfo : fetchNoticeInfo,
				searchNotices : searchNotices,
				viewCase : viewCase,
				testApi: testApi
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
		
		function viewCase(item1,item2){
					var data = {"refId" : item1,"type" :item2, "sessID" : localStorageService.get('sessionID'), "userID" : localStorageService.get('payrollNo')};
					return $http({
			            method: 'POST',
			            url: viewAddCaseURL,
			            headers: {
			            	'Authorization' : authHeader,
			            	//'Authorization' : localStorageService.get('authHeader'),
			            	'Content-Type': 'application/xml; charset=utf-8'
			            },
			            data:data
			        });
				}
		
		
		function testApi(data){
					return $http({
			            method: 'POST',
			            url: testApiURL,
			            headers: {		            	
			            		'Authorization' : authHeader,
			            	'Content-Type': 'application/json; charset=utf-8'
			            },
			            data:data
			        });
				}
		
		
	}
})();
