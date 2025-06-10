(function() {
	'use strict';

	angular
		.module('regCalApp.addcase', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('addcase', {
					url: '/dashboard/addcase',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/addcase/addcase.html',
								controller : 'AddCaseController as vm'
					        }
					      } 
					
					
				});
		});
})();