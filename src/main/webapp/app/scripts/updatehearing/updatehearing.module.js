(function() {
	'use strict';

	angular
		.module('regCalApp.updatehearing', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('updatehearing', {
                    url: '/dashboard/updatehearing',
                    views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/updatehearing/updatehearing.html',
					            controller : 'UpdateHearingController as vm'
					        }
					      } 
					
					     
					
				});
		});
})();