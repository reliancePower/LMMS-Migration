(function() {
	'use strict';

	angular
		.module('regCalApp.approveforum', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('approveforum', {
					url: '/dashboard/approveforum',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/approveforum/approveforum.html',
								controller : 'ApproveForumController as vm'
					        }
					      } 
					
					
				});
		});
})();