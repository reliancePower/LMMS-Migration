(function() {
	'use strict';

	angular
		.module('regCalApp.appstore')
		.controller('AppStoreController', AppStoreController);

	AppStoreController.$inject = ['$state', 'ApproveDeviceService', 'DTOptionsBuilder', 'DTColumnBuilder','$scope', 'localStorageService','$compile', '$window'];

	/* @ngInject */
	function AppStoreController($state,  ApproveDeviceService, DTOptionsBuilder, DTColumnBuilder, $scope, localStorageService, $compile, $window) {
		var vm = angular.extend(this, {			
			
			//releaseDate : new Date('2018-11-03'),
			//releaseDate : new Date('2019-06-22'),
			//releaseDate : new Date('2019-09-13')
			releaseDate : new Date('2019-11-14')
	
		});	
		 (function activate() {
			 if(localStorageService.get('payrollNo') == undefined || localStorageService.get('payrollNo') == null){
				 $state.go('login');
				 return false;
			 }
	          
		    })();
		
		 
	}
})();
