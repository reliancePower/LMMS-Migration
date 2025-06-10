(function() {
	'use strict';

	angular
		.module('regCalApp')
		.config(function($httpProvider) {
			$httpProvider.interceptors.push(function($rootScope, $q) {
				return {
					request: function(config) {
						$rootScope.$broadcast('loading:show');
						return config;
					},
					response: function(response) {
						$rootScope.$broadcast('loading:hide');
						return response;
					},
					requestError: function(rejectReason) {
						$rootScope.$broadcast('loading:hide');
						return $q.reject(rejectReason);
 					},
 					responseError: function(rejectReason) {
						$rootScope.$broadcast('loading:hide');
						return $q.reject(rejectReason);
 					}
				};
			});
		})
		.run(function($rootScope, usSpinnerService) {
			$rootScope.$on('loading:show', function() {
				usSpinnerService.spin('spinner-1');
				
			});

			$rootScope.$on('loading:hide', function() {
				usSpinnerService.stop('spinner-1');
				
			});
		});
})();
