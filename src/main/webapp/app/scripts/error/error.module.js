(function() {
	'use strict';

	angular
		.module('regCalApp.error', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('error', {
					url: '/error',
					 views: {
				            'nav': {
				                templateUrl: null,
				                controller: null
				            },
				            'content': {
				            	templateUrl: 'app/scripts/error/error.html',
								controller : 'ErrorController as vm'
				            }
				        }
					
					
				});
		});
})();