(function() {
	'use strict';

	angular
		.module('regCalApp.addnotice', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('addnotice', {
					url: '/dashboard/addnotice',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/addnotice/addnotice.html',
								controller : 'AddNoticeController as vm'
					        }
					      } 
					
					
				});
		});
})();