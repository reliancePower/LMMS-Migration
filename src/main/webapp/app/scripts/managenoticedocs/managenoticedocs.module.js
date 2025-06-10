(function() {
	'use strict';

	angular
		.module('regCalApp.managenoticedocs', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('managenoticedocs', {
                    url: '/dashboard/managenoticedocs',
                    views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/managenoticedocs/managenoticedocs.html',
					            controller : 'ManageNoticesDocsController as vm'
					        }
					      } 
					
					     
					
				});
		});
})();