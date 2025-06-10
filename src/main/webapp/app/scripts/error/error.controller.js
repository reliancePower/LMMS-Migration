(function() {
	'use strict';

	angular
		.module('regCalApp.error')
		.controller('ErrorController', ErrorController);

	ErrorController.$inject = ['$state',  'loginService', '$scope', 'localStorageService', '$watch'];

	/* @ngInject */
	function ErrorController($state, loginService, $scope, localStorageService, $watch) {
		var vm = angular.extend(this, {
//			createCaptcha : createCaptcha,

			

		});

		
	}
})();
