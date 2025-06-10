(function() {
	'use strict';

	angular
		.module('regCalApp.gst', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('gst', {
					url: '/dashboard/gst',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/gst/gst.html',
								controller : 'GstController as vm'
					        }
					      } 
					
					
				});
		});
})();

