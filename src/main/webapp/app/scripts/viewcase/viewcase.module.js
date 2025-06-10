(function() {
	'use strict';

	angular
		.module('regCalApp.viewcase', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('viewcase', {
					url: '/dashboard/viewcase',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/viewcase/viewcase.html',
								controller : 'ViewCaseController as vm'
					        }
					      } 
					
					
				});
		});
})();

