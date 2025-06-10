(function() {
	'use strict';

	angular
		.module('regCalApp.login')
		.controller('LoginController', LoginController);

	LoginController.$inject = ['$state',  'loginService', '$scope', 'localStorageService', '$window','$uibModal', '$uibModalStack', 'dashboardService'];

	/* @ngInject */
	function LoginController($state, loginService, $scope, localStorageService, $window, $uibModal, $uibModalStack, dashboardService ) {
		var vm = angular.extend(this, {
			createCaptcha : createCaptcha,
			validateCaptcha : validateCaptcha,
			submit: submit,	
			getRadioValue : getRadioValue,
			form: {
				userName: null,
				password: null,
				captchaCode : null,
				dob : '',
				userType : 'employee',
				captchaActualCode : null
			},
			showCaptaError : false
			

		});
		function getRadioValue(val){
			vm.form.userType = val;
		}
		function validateCaptcha() {
		    var string1 = removeSpaces(vm.captchaActualCode);
		    var string2 = removeSpaces(vm.captchaCode);
		    if (string1 == string2) {
		    	vm.showCaptaError = false;
		    }
		    else {
		    	vm.showCaptaError = true;
		    }
		}
		
		// ******************************************************
		
		 function removeSpaces(string) {
			 if(string != null)
				 return string.split(' ').join('');
		}
		 
		// ******************************************************
		
		  function createCaptcha() {
		    var alpha = new Array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',  'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z','1','2','3','4','5','6','7','8','9');
		    var i;
		    var code = "";
		    for (i = 0; i < 5; i++) {
		        code = code + alpha[Math.floor(Math.random() * alpha.length)] + "";
		    }
		    vm.captchaCode = code
		}
		  
		function submit(form) {
			validateCaptcha();
			angular.forEach(form, function(obj) {
				if(angular.isObject(obj) && angular.isDefined(obj.$setDirty)) { 
					obj.$setDirty();
				}
			})
					
			if (form.$valid && !vm.showCaptaError) {
				var deviceType = getBrowser();
				var iv = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
		        var salt = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);

		        var aesUtil = new AesUtil(128, 1000);
		        var ciphertext = aesUtil.encrypt(salt, iv, '1234567891234567', form.password.$modelValue);

		        var aesPassword = (iv + "::" + salt + "::" + ciphertext);
		        var password = btoa(aesPassword);
		        vm.form.password = password;
				var jsonReq = {"userID" : form.userName.$modelValue, "password" : password , "userType" : vm.form.userType, "deviceType":deviceType, 'dob' :form.dob.$modelValue }
				loginService.login(jsonReq)
				.then(function(result) {
	
					if(result.data.status == 'T' ) {
						localStorageService.set('userName', result.data.userName);
						localStorageService.set('payrollNo', result.data.payrollNo);	
						localStorageService.set('mobileNo', result.data.mobileNo);
						localStorageService.set('webMailID', result.data.webMailID);
						localStorageService.set('userType', result.data.userType);
						localStorageService.set('accessCtrl', result.data.accessCtrl);
						localStorageService.set('forum', result.data.forum);
						localStorageService.set('userPlant', result.data.userPlant);
						localStorageService.set('sessionID', result.data.sessionID);
						localStorageService.set('caseType', result.data.caseType);
						localStorageService.set('company', result.data.company);
						localStorageService.set('caseCategory', result.data.caseCategory);
						localStorageService.set('emailID', result.data.emailID);
						localStorageService.set('dob', result.data.dob);
						localStorageService.set('lastJobRun', result.data.lastJobRun);
						localStorageService.set('imeiNo1', result.data.imeiNo1);
						localStorageService.set('imeiNo2', result.data.imeiNo2);
						localStorageService.set('imeiNo3', result.data.imeiNo3);
						localStorageService.set('deviceText1', result.data.deviceText1);
						localStorageService.set('deviceText2', result.data.deviceText2);
						localStorageService.set('deviceText3', result.data.deviceText3);
						localStorageService.set('maxUsageID', result.data.maxUsageID);
						localStorageService.set('caseDocView', result.data.caseDocView);
	                    localStorageService.set('noticeDocView', result.data.noticeDocView);
	                   
	                    dashboardService.getHomePageInfo().then(function(result) {
							if((result.data.status == 'F') || (result.data.todayHearingArray == undefined)){
								alert(result.data.msg);
								$state.reload();
								return false;
								 
							}
							vm.lastLogin = result.data.lastLoginTime;
							localStorageService.set('lastLogin', vm.lastLogin);
							$state.go('dashboard');
//							var modalInstance = $uibModal.open({			      
//						        ariaLabelledBy: 'modal-title',
//						        ariaDescribedBy: 'modal-body',
//						        templateUrl: 'msgBoard.html',			        	        
//						        size: 'lg',
//								backdrop : 'static',
//								keyboard : false,
//								 windowClass: 'userFillWindowTop',
//						        scope : $scope			       
//						      });
//
//						      modalInstance.result.then(function (response) {			       
//						      }, function () {
//						        console.log('Modal dismissed at: ' + new Date());
//						        $state.go('dashboard');
//						      });
							vm.notificationFlag = result.data.notificationStatus;
							localStorageService.set('userType', result.data.userType);
							

						}, function caseError(error) {
							alert('There seems some problem. Please try again later...');
							 $state.reload();
							return false;
						});
												
					}else {
                        alert(result.data.msg);
                        $state.reload();
						
						
					}
				}, function loginError(error){
									
		          alert('Something went wrong... Pls try again...');
		          $state.reload();
		         return false;
		       });
				
			}else
				return;
			
		}
		function getBrowser(){
	
			var currBrowser = '';
		    // Opera 8.0+
		    var isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
		
		    // Firefox 1.0+
		    var isFirefox = typeof InstallTrigger !== 'undefined';
		
		    // Safari 3.0+ "[object HTMLElementConstructor]" 
		    var isSafari = /constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || safari.pushNotification);
		
		    // Internet Explorer 6-11
		    var isIE = /*@cc_on!@*/false || !!document.documentMode;
		
		    // Edge 20+
		    var isEdge = !isIE && !!window.StyleMedia;
		
		    // Chrome 1+
		    var isChrome = !!window.chrome && !!window.chrome.webstore;
		
		    // Blink engine detection
		    var isBlink = (isChrome || isOpera) && !!window.CSS;
		    
		    if(isOpera)
		    	currBrowser = 'Opera';
		    else if(isFirefox)
		    	currBrowser = 'Firefox';
		    else if(isSafari)
		    	currBrowser = 'Safari';
		    else if(isIE)
		    	currBrowser = 'IE';
		    else if(isEdge)
		    	currBrowser = 'Edge';
		    else if(isChrome)
		    	currBrowser = 'Chrome';
		    else if(isBlink)
		    	currBrowser = 'Blink';
		    else
		    	currBrowser = 'Mobile';
		    
		    return currBrowser;
		
		
		}
		
	}
})();
