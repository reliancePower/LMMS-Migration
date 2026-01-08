(function() {
	'use strict';

	angular
		.module('regCalApp.ai', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('ai', {
					url: '/dashboard/ai',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/ai/ai.html',
								controller : 'AiController as vm'
					        }
					      } 
					
					
				});
		});
})();

