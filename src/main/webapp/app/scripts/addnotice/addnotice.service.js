(function() {
	'use strict';

	angular
		.module('regCalApp.addcase')
		.factory('AddNoticeService', AddNoticeService);

	AddNoticeService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function AddNoticeService(localStorageService, $http) {
		var service = {
			fetchNoticeSection : fetchNoticeSection,
			addNewNotice : addNewNotice

		};
		return service;
		
		
		
		function fetchNoticeSection(item) {	
			var data = {"userID" : localStorageService.get('payrollNo'), "type" : "fetchSection", "sessID" : localStorageService.get('sessionID'), "noticeType" : item}
			
			return $http({
		            method: 'POST',
		            url: noticeManageURL,
		            headers: {
		            	
		            	'Authorization' :authHeader,
		            	'Content-Type': 'application/json; charset=utf-8'
		            },
		            data:data
		        });
							   
			}
		
		function addNewNotice(data){
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
		

	}
})();
