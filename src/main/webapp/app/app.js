angular
		.module(
				'regCalApp',
				[ 'ui.router', 'ui.bootstrap', 'ngSanitize', 'LocalStorageModule',
						'angularSpinner', 'ngIdle',
						'datatables', 'datatables.light-columnfilter',
						'datatables.buttons', 'datatables.bootstrap',
						'textAngular', 'regCalApp.login',
						'regCalApp.dashboard', 'regCalApp.addcase',
						'regCalApp.viewcase', 'regCalApp.editcase','regCalApp.viewadditionalnotice',
						'regCalApp.adduser', 'regCalApp.viewuser',
						'regCalApp.approvedevice', 'regCalApp.appstore',
						'regCalApp.addnotice', 'regCalApp.viewnotice','regCalApp.gst',
						'regCalApp.searchcase', 'regCalApp.updatehearing',
						'regCalApp.managedocs', 'regCalApp.pvforumupdatelog',
						'regCalApp.manageforum', 'regCalApp.approveforum',
						'regCalApp.managenoticedocs', 'hm.readmore',
						'ui.select', 'elif', 'regCalApp.managenotes' ])

		.run(
				function($state, $rootScope, localStorageService, Idle) {
					Idle.watch();
					$rootScope.nav = false;
					if (localStorageService.get('payrollNo') != undefined
							|| localStorageService.get('payrollNo') != null) {

						$rootScope.$on('$stateChangeStart', function(event,
								toState, toParams, fromState, fromParams) {
							if (toState.name === 'login') {// toState variable
															// see the state
															// you're going
								$rootScope.nav = false;
							} else {
								$rootScope.nav = true;
							}
						});
					} else
						$state.go('login');

				})
		.filter('pagination', function() {
			return function(input, currentPage, pageSize) {
				if (angular.isArray(input)) {
					var start = (currentPage - 1) * pageSize;
					var end = currentPage * pageSize;
					return input.slice(start, end);
				}
			};
		})
		.filter(
				'totalSum',
				function() {
					return function(data, key1, key2, key3) {
						if (angular.isUndefined(data)
								|| angular.isUndefined(key1)
								|| angular.isUndefined(key2)
								|| angular.isUndefined(key3))
							return 0;

						var sum = 0;
						angular
								.forEach(
										data,
										function(v, k) {
											sum = sum
													+ (parseInt(v[key1])
															+ parseInt(v[key2]) + parseInt(v[key3]));
										});
						return sum;
					}
				})
		.directive('numOnly', function() {
			return {
				require : 'ngModel',
				link : function(scope, element, attr, ngModelCtrl) {
					function fromUser(text) {
						if (text) {
							var transformedInput = text.replace(/[^0-9]/g, '');

							if (transformedInput !== text) {
								ngModelCtrl.$setViewValue(transformedInput);
								ngModelCtrl.$render();
							}
							return transformedInput;
						}
						return undefined;
					}
					ngModelCtrl.$parsers.push(fromUser);
				}
			};
		})
		.directive(
				'safePaste',
				[ function() {
					/*
					 * Private Variables
					 */

					// Holds special characters to be replaced with safe
					// versions
					var specialCharacters = [ "–", "’" ], normalCharacters = [
							"-", "'" ]

					/*
					 * Private Methods
					 */

					// Replaces invalid characters with safe versions
					function replaceInvalidCharacters(string) {
						var regEx;

						// Loop the array of special and normal characters
						for (var x = 0; x < specialCharacters.length; x++) {
							// Create a regular expression to do global replace
							regEx = new RegExp(specialCharacters[x], 'g');

							// Do the replace
							string = string.replace(regEx, normalCharacters[x]);
						}

						return string;
					}

					// Does the magic
					function handlePaste(event) {

						// We got this
						event.preventDefault();

						// Get the plain text
						var plainText = "";

						if (window.clipboardData
								&& window.clipboardData.getData) { // IE
							plainText = window.clipboardData.getData('Text');
						} else if (event.originalEvent.clipboardData
								&& event.originalEvent.clipboardData.getData) { // other
																				// browsers
							plainText = event.originalEvent.clipboardData
									.getData('text/plain');
						}

						// Clean up the text
						var cleanText = replaceInvalidCharacters(plainText);

						// Tell the browser to insert the text
						// document.execCommand('insertText', false, cleanText);
						if (window.clipboardData) {

							if (window.getSelection) {
								var selObj = window.getSelection();
								var selRange = selObj.getRangeAt(0);
								selRange.deleteContents();
								selRange.insertNode(document
										.createTextNode(cleanText));
							}
						} else if (event.originalEvent.clipboardData) {

							document
									.execCommand('insertText', false, cleanText);
						}

						// Backup to the event.preventDefault()
						return false;
					}

					/*
					 * Declaration
					 */
					var declaration = {};

					declaration.restrict = 'A';

					declaration.link = function(scope, element, attr) {
						// Attach the paste handler
						element.on('paste', handlePaste);

						// Register to remove the paste handler
						scope.$on('$destroy', function() {
							element.off('paste', handlePaste);
						});
					};

					return declaration;
				} ])
		.directive(
				'contenteditable',
				[
						'$sce',
						function($sce) {
							return {
								restrict : 'A', // only activate on element
												// attribute
								require : '?ngModel', // get a hold of
														// NgModelController
								link : function(scope, element, attrs, ngModel) {
									if (!ngModel)
										return; // do nothing if no ng-model

									// Specify how UI should be updated
									ngModel.$render = function() {
										element
												.html($sce
														.getTrustedHtml(ngModel.$viewValue
																|| ''));
										read(); // initialize
									};

									// Listen for change events to enable
									// binding
									element.on('blur keyup change', function() {
										scope.$evalAsync(read);
									});

									// Write data to the model
									function read() {
										var html = element.html();
										// When we clear the content editable
										// the browser leaves a <br> behind
										// If strip-br attribute is provided
										// then we strip this out
										if (attrs.stripBr
												&& (html == '<br>' || html == 'br')) {
											html = '';
										}
										ngModel.$setViewValue(html);
									}
								}
							};
						} ])
		.directive(
				'speclchars',
				function() {
					return {
						require : 'ngModel',
						link : function(scope, element, attr, ngModelCtrl) {
							function fromUser(text) {
								if (text) {
									var transformedInput = text.replace(
											/[^ 0-9A-Za-z.,@/-]/g, '');

									if (transformedInput !== text) {
										ngModelCtrl
												.$setViewValue(transformedInput);
										ngModelCtrl.$render();
									}
									return transformedInput;
								}
								return undefined;
							}
							ngModelCtrl.$parsers.push(fromUser);
						}
					};
				})
		.filter(
				'titleCase',
				function() {
					return function(input) {
						input = input || '';
						input = input.replace(/_/g, ' ');
						return input.replace(/\w\S*/g, function(txt) {
							return txt.charAt(0).toUpperCase()
									+ txt.substr(1).toLowerCase();
						});
					};
				})
		.filter('abs', function() {
			return function(val) {
				return Math.abs(val);
			};
		})
		.filter('removeHTMLTags', function() {
			return function(text) {
				return text ? String(text).replace(/<[^>]+>/gm, '') : '';
			};
		})
		.filter('momentDate', function() {
			return function(value, format) {
				if (!value)
					return "";

				return moment(value).format(format);
			}
		})
		.config(
				[
						'$locationProvider',
						'localStorageServiceProvider',
						'$urlRouterProvider',
						function($locationProvider,
								localStorageServiceProvider, $urlRouterProvider) {
							$locationProvider.hashPrefix('');
							localStorageServiceProvider.setPrefix('user')
									.setStorageType('localStorage');
							$urlRouterProvider.otherwise('login');
							// $locationProvider.html5Mode(true);

						} ]).config(
				[ 'KeepaliveProvider', 'IdleProvider',
						function(KeepaliveProvider, IdleProvider) {
							IdleProvider.idle(7200);
							IdleProvider.timeout(30);
							KeepaliveProvider.interval(30);
						} ]).config(
				[
						'$provide',
						function($provide) {
							$provide.decorator('$state', [
									'$delegate',
									'$window',
									function($delegate, $window) {

										var extended = {
											goToNewTab : function(stateName,
													params) {
												$window.open($delegate.href(
														stateName, params),
														'_blank');
											}
										};

										angular.extend($delegate, extended);
										return $delegate;
									} ]);
						} ]);
