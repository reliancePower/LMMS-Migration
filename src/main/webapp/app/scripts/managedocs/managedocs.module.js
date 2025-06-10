(function() {
	'use strict';

	angular
		.module('regCalApp.managedocs', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('managedocs', {
                    url: '/dashboard/managedocs',
                    views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/managedocs/managedocs.html',
					            controller : 'ManageDocsController as vm'
					        }
					      } 
					
					     
					
				});
		});
})();