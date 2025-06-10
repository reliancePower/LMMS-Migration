(function() {
	'use strict';

	angular
		.module('regCalApp.viewnotice', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('viewnotice', {
					url: '/dashboard/viewnotice',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/viewnotice/viewnotice.html',
								controller : 'ViewNoticeController as vm'
					        }
					      } 
					
					
				});
		});
})();

