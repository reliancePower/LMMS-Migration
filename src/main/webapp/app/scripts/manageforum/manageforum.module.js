(function() {
	'use strict';

	angular
		.module('regCalApp.manageforum', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('manageforum', {
                    url: '/dashboard/manageforum',
                    views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/manageforum/manageforum.html',
					            controller : 'ManageForumController as vm'
					        }
					      } 
					
					     
					
				});
		});
})();