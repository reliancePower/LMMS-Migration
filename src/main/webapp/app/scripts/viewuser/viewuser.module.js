(function() {
	'use strict';

	angular
		.module('regCalApp.viewuser', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('viewuser', {
					url: '/dashboard/viewuser',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/viewuser/viewuser.html',
								controller : 'ViewUserController as vm'
					        }
					      } 
					
					
				});
		});
})();

