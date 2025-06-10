(function() {
	'use strict';

	angular
		.module('regCalApp.viewadditionalnotice')
		.factory('viewAdditionalNoticeService', viewAdditionalNoticeService);

	viewAdditionalNoticeService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function viewAdditionalNoticeService(localStorageService, $http) {

			var viewDetails = '';
			var service = {

				
			};
			return service;				        
			
		}
})();
