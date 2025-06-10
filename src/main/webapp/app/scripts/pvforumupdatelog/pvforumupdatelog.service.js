(function() {
	'use strict';

	angular.module('regCalApp.pvforumupdatelog').factory(
			'PVForumUpdateService', PVForumUpdateService);

	PVForumUpdateService.$inject = [ 'localStorageService', '$http' ];

	/* @ngInject */
	function PVForumUpdateService(localStorageService, $http) {

		var service = {
			viewAllLogs : viewAllLogs,
			fetchNewForums : fetchNewForums

		};
		return service;

		function viewAllLogs(data) {

			return $http({
				method : 'POST',
				url : pvForumUpdateLogURL,
				headers : {
					'Authorization' : authHeader,
					//'Authorization' : localStorageService.get('authHeader'),
					'Content-Type' : 'application/xml; charset=utf-8'
				},
				data : data
			});
		}
		
		function fetchNewForums() {

			return $http({
				method : 'GET',
				url : fetchNewForumsURL,
				headers : {
					'Authorization' : pvAuthHeader,
					//'Authorization' : localStorageService.get('authHeader'),
					'Content-Type' : 'application/xml; charset=utf-8'
				}
			});
		}
		
		

	}
})();
