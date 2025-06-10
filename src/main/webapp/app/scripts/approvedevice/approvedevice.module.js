(function() {
	'use strict';

	angular
		.module('regCalApp.approvedevice', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('approvedevice', {
					url: '/dashboard/approvedevice',
					 views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/approvedevice/approvedevice.html',
								controller : 'ApproveDeviceController as vm'
					        }
					      } 
					
					
				});
		});
})();