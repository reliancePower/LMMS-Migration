(function() {
	'use strict';

	angular
		.module('regCalApp.dashboard', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('dashboard', {
					url: '/dashboard',
					cache : true,
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/dashboard/dashboard.html',
								controller : 'DashboardController as vm'
					        }
					      } 
					
					
				});
		});
})();