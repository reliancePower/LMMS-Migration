(function() {
	'use strict';

	angular
			.module('regCalApp.pvforumupdatelog', [ 'ui.router' ])
			.config(
					function($stateProvider) {
						$stateProvider
								.state(
										'pvforumupdatelog',
										{
											url : '/dashboard/pvforumupdatelog',
											views : {
												'nav' : {
													templateUrl : 'app/view/navbar.html',
													controller : 'NavBarController as vm'
												},
												'content' : {
													templateUrl : 'app/scripts/pvforumupdatelog/pvforumupdatelog.html',
													controller : 'PVForumUpdateController as vm'
												}
											}

										});
					});
})();