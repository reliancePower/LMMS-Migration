(function() {
	'use strict';

	angular
		.module('regCalApp.searchcase', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('searchcase', {
					url: '/dashboard/searchcase',
					cache : true,
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/searchcase/searchcase.html',
								controller : 'SearchCaseController as vm'
					        }
					      } 
					
					
				});
		});
})();

