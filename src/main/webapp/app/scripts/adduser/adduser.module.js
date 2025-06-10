(function() {
	'use strict';

	angular
		.module('regCalApp.adduser', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('adduser', {
					url: '/dashboard/adduser',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/adduser/adduser.html',
								controller : 'AddUserController as vm'
					        }
					      } 
					
					
				});
		});
})();