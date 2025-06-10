(function() {
	'use strict';

	angular
		.module('regCalApp.appstore', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('appstore', {
					url: '/dashboard/appstore',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/appstore/appstore.html',
								controller : 'AppStoreController as vm'
					        }
					      } 
					
					
				});
		});
})();