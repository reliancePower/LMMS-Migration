(function() {
	'use strict';

	angular
		.module('regCalApp.viewai')
		.factory('viewAiService', viewAiService);

	viewAiService.$inject = ['localStorageService', '$http'];

	/* @ngInject */
	function viewAiService(localStorageService, $http) {

		var service = {
			saveFeedBack: saveFeedBack
		};
		return service;
		
		function saveFeedBack(data){
							return $http({
					            method: 'POST',
					            url: saveFeedBackURL,
					            headers: {		            	
					            		'Authorization' : authHeader,
					            	'Content-Type': 'application/json; charset=utf-8'
					            },
					            data:data
					        });
						}
        
		
	}
})();
