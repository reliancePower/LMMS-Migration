(function() {
	'use strict';

	angular
		.module('regCalApp.managenotes', [
			'ui.router'
		])
		.config(function($stateProvider) {
			$stateProvider
				.state('managenotes', {
                    url: '/dashboard/managenotes',
                    views: {
					        'nav': {
					          templateUrl: 'app/view/navbar.html',
					          controller : 'NavBarController as vm'
					        },
					        'content': {
					        	templateUrl: 'app/scripts/managenotes/managenotes.html',
					            controller : 'ManageNotesController as vm'
					        }
					      } 
					
					     
					
				});
		});
})();