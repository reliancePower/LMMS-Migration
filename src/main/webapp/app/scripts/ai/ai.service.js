(function() {
	'use strict';

	angular
		.module('regCalApp.ai')
		.factory('aiService', aiService);

	aiService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function aiService(localStorageService, $http) {

			var viewDetails = '';
			var service = {

				
			};
			return service;				        
			
		}
})();
