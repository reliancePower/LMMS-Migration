(function() {
	'use strict';

	angular
		.module('regCalApp.login', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('login', {
					url: '/login',
					 views: {
				            'nav': {
				                templateUrl: null,
				                controller: null
				            },
				            'content': {
				            	templateUrl: 'app/scripts/login/login.html',
								controller : 'LoginController as vm'
				            }
				        }
					
					
				});
		});
})();