(function() {
	'use strict';

	angular
		.module('regCalApp.editcase', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('editcase', {
					url: '/dashboard/editcase',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/editcase/editcase.html',
								controller : 'EditCaseController as vm'
					        }
					      } 
					
					
				});
		});
})();

