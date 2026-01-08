(function() {
	'use strict';

	angular
		.module('regCalApp.viewai', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('viewai', {
					url: '/dashboard/viewai',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/viewai/viewai.html',
								controller : 'ViewAiController as vm'
					        }
					      } 
					
					
				});
		});
})();

