angular
		.module('macdModule', [ 'nvd3', 'ohlcServiceModule', 'macdServiceModule' ])
		.controller(
				'MacdController',
				[
						'$http',
						'$scope',
						'$location',
						'ohlcService',
						'macdService',
						function MacdController($http, $scope, $location, ohlcService, macdService) {
							
							this.getParameterByName = function getParameterByName(url, name) {
							    if (!url) url = window.location.href;
							    name = name.replace(/[\[\]]/g, "\\$&");
							    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
							        results = regex.exec(url);
							    if (!results) return null;
							    if (!results[2]) return '';
							    return decodeURIComponent(results[2].replace(/\+/g, " "));
							}

							var grainParam = this.getParameterByName($location.absUrl(), 'grain');
							console.log(grainParam);
							
							if(typeof grainParam == "undefined") {								
								this.echelle = "15";
							} else {
								this.echelle = grainParam;
							}

							this.echelles = {
								"1" : "1 minute",
								"5" : "5 minutes",
								"15" : "15 minutes",
								"30" : "30 minutes",
								"60" : "60 minutes",
								"240" : "4 heures",
								"1440" : "24 heures",
								"10080" : "1 semaine",
								"21600" : "15 jours"
							};

							/* OHLC */
							$scope.ohlcOptions = ohlcService.options;

							this.ohlcRefresh = function() {
								ohlcService.refresh(this.echelle.toString())
									.then(function(response) {
										$scope.api.refresh();
										$scope.api.updateWithData(response.data);
										
									});
							};
							
							this.ohlcRefresh();
							
							
							/* MACD */
							$scope.macdOptions = macdService.options;

							this.macdRefresh = function() {
								macdService.refresh(this.echelle.toString())
									.then(function(response) {
									$scope.macdData = response.data;
								});
							};

							this.macdRefresh();
							
							this.refresh = function refresh() {
								this.ohlcRefresh();
								this.macdRefresh();
							};

						} ]);