(function() {
	'use strict';

	angular
		.module('regCalApp.addcase')
		.controller('AddNoticeController', AddNoticeController);
	
	AddNoticeController.$inject = ['$state', 'addCaseService', '$scope', 'localStorageService','DTOptionsBuilder', 'DTColumnBuilder', '$uibModal', '$uibModalStack', 'ViewNoticeService', '$sce','$window', 'dashboardService', 'AddNoticeService', 'navbarService'];

	/* @ngInject */
	function AddNoticeController($state,  addCaseService, $scope, localStorageService, DTOptionsBuilder, DTColumnBuilder, $uibModal,  $uibModalStack, ViewNoticeService, $sce, $window, dashboardService, AddNoticeService, navbarService) {
		var vm = angular.extend(this, {
			submit: submit,
			fetchAllMaster : fetchAllMaster,			
			form: {
				company: '',
				business: '',
				vertical : '',
				caseType : '',
				state : 0,
				panNo : '',
				assessYear : '',
				noticeType : '',
				section : 0,
				sectionDesc : '',
				addressedTo : '',
				issuedByName : '',
				issuedByDesig : '',
				issuedByOrganization : '',
				natureOfParty : '',
				briefFacts : '',
				stakeInvolment : '',
				dateOnNotice : '',
				dateOnWhichNoticeReceived : '',
				dateOnWhichNoticeRequiredToReply : '',
				dateOfReply : '',
				remarks : '',
				status : 0,
				priority : 0
			},
			companyInfo : [],
			businessInfo : [],
			verticalInfo : [],	
			caseTypeInfo : [],
			stateInfo : [],
			priorityInfo : [],
			noticeTypeInfo : [],
			noticeStatusInfo : [],
			sectionInfo : [],
			years : [],
			checkYear : checkYear,
			yearSel : false,
			fetchVerticalMaster : fetchVerticalMaster,
			fetchCompanyMaster : fetchCompanyMaster,
			selComp : '',
			fetchNoticeSection : fetchNoticeSection,
			fetchSectionDesc : fetchSectionDesc,
			selNoticyType : ''
			
				
		});	
		 (function activate() {
			 if(localStorageService.get('payrollNo') == undefined || localStorageService.get('payrollNo') == null){
				 $state.go('login');
				 return false;
			 }
			 fetchAllMaster();
			 localStorageService.remove('searchCaseDetails');
			 var d = new Date();
			 var n = d.getFullYear();
			 var next = n+1;
			 for(var i = 1990; i <= next; i++)	{
				 var t = i+1;
				 vm.years.push(i + '-' + t);
			 }
			 
			
			 
		 })();
		 
		 
		 function checkYear(){
			 var sel = $('#caseType').val();			 
			 if(parseInt(sel) == 1){
				 $('#hiddenContainer1').css('display','block');	
				 $('#hiddenContainer2').css('display','block');	
				 $('#hiddenContainer3').css('display','block');	
				 $('#hiddenContainer4').css('display','block');	
				 $('#hiddenContainer5').css('display','block');	
				 $('#typeContainer').css('display','none');
				 vm.yearSel = true;
			 }else {
				 $('#hiddenContainer1').css('display','none');
				 $('#hiddenContainer2').css('display','none');
				 $('#hiddenContainer3').css('display','none');
				 $('#hiddenContainer4').css('display','none');
				 $('#hiddenContainer5').css('display','none');
				 $('#typeContainer').css('display','block');
				 vm.yearSel = false;
			 }
				 
		 }
		 
		 
		
		function fetchAllMaster(){
			ViewNoticeService.fetchMaster()
    		.then(function(result) {
    			if(result.data.status == 'S'){
					alert(result.data.msg);
					if (localStorageService.get('payrollNo') == undefined
							|| localStorageService.get('payrollNo') == '') {
						$window.localStorage.clear();
						localStorageService.clearAll;
						localStorageService.cookie.clearAll;
						$state.go('login');
					} else {
						navbarService
								.logout()
								.then(
										function(result) {
											$window.localStorage.clear();
											localStorageService.clearAll;
											localStorageService.cookie.clearAll;
											$state.go('login');
										},
										function caseError(error) {
											localStorageService.clearAll;
											localStorageService.cookie.clearAll;
											$state.go('login');
											console
													.log('There seems some problem. Please try again later...');

										});
					}
				}
//    			if(result.data.status == 'F'){
//					alert(result.data.msg);
//					return false;
//				}
    			vm.companyInfo = result.data.companyArray;    			
    			vm.caseTypeInfo = result.data.caseTypeArray;
    			vm.noticeTypeInfo = result.data.noticeTypeArray;
    			vm.noticeStatusInfo = result.data.noticeStatusArray
    			vm.stateInfo =  result.data.stateArray;
    			vm.priorityInfo =  result.data.priorityArray;
			}, function caseError(error){
		      alert('There seems some problem. Please try again later...');
		      return false;
		    });
		}
		
		function fetchCompanyMaster(item){
			vm.selComp = item;
			dashboardService.fetchCompanyMaster(item)
    		.then(function(result) {
    			if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
    			vm.businessInfo = result.data.businessMaster;    			
    		
			
			}, function caseError(error){
		      alert('There seems some problem. Please try again later...');
		      return false;
		    });
		}
	
		
		function fetchVerticalMaster(item){
			vm.selBusiness = item;
			dashboardService.fetchVerticalMaster(item)
    		.then(function(result) {
    			if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
    			vm.verticalInfo = result.data.verticalMaster;			
			}, function caseError(error){
		      alert('There seems some problem. Please try again later...');
		      return false;
		    });
		}
		
		
		function fetchNoticeSection(item){
			vm.selNoticyType = item;
			AddNoticeService.fetchNoticeSection(item)
    		.then(function(result) {
    			if(result.data.status == 'F'){
					alert(result.data.msg);
					return false;
				}
    			vm.sectionInfo = result.data.noticeSectionMaster;			
			}, function caseError(error){
		      alert('There seems some problem. Please try again later...');
		      return false;
		    });
		}
		
		function fetchSectionDesc(section){
			var temp = _.findWhere(vm.sectionInfo, {id: section});
			vm.form.sectionDesc = temp.desc;
		}
		
		
		function submit(form) {
			angular.forEach(form, function(obj) {
				if(angular.isObject(obj) && angular.isDefined(obj.$setDirty)) { 
					obj.$setDirty();
				}
			})
					
			if (form.$valid) {
				
				var jsonReq = {"company" : form.company.$modelValue, "business" : form.business.$modelValue , "vertical" : parseInt(form.vertical.$modelValue), "caseType" : parseInt(form.caseType.$modelValue),
								"type" : 'add', "asstYear" : form.assessYear.$modelValue, "panNo" : form.panNo.$modelValue,"noticeType" : form.noticeType.$modelValue, "state" : form.state.$modelValue,
								"noticeSection" : form.section.$modelValue,"issuedByName" : form.issuedByName.$modelValue, "issuedByDesig" : form.issuedByDesig.$modelValue, "issuedByOrganization" : form.issuedByOrganization.$modelValue,
								"dateOnNotice" : form.dateOnNotice.$modelValue, "dateOnWhichNoticeReceived" : form.dateOnWhichNoticeReceived.$modelValue, "dateOnWhichNoticeRequiredToReply" : form.dateOnWhichNoticeRequiredToReply.$modelValue,
								"dateOfReply" : form.dateOfReply.$modelValue, "remarks" : form.remarks.$modelValue, "status" : form.status.$modelValue,
								"enteredBy": localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID'), "addressedTo" : form.addressedTo.$modelValue,
								"natureOfParty" : form.natureOfParty.$modelValue, "briefFacts" : form.briefFacts.$modelValue, "stakeInvolment" : form.stakeInvolment.$modelValue, "priority" : form.priority.$modelValue}
			
				AddNoticeService.addNewNotice(jsonReq)
				.then(function(result) {
	
					if(result.data.status == 'T' ) {					
						alert('Notice details added successfully with ID - '+result.data.id);
						$state.reload();
						//$window.location.reload();
						
						
					}else {
						alert(result.data.msg);							
						return false;
					}
				}, function loginError(error){
									
		          alert('Something went wrong... Pls try again...');		         
		         return false;
		       });
				
			}else
				return;
			
		}
		
			
	}
	
})();
