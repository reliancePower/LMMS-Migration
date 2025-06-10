(function() {
	'use strict';

	angular
		.module('regCalApp.viewadditionalnotice', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('viewadditionalnotice', {
					url: '/dashboard/viewadditionalnotice',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/viewadditionalnotice/viewadditionalnotice.html',
								controller : 'ViewAdditionalNoticeController as vm'
					        }
					      } 
					
					
				});
		});
})();

