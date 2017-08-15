angular
		.module('macdModule', [ 'nvd3', 'ohlcServiceModule', 'macdServiceModule' ])
		.controller(
				'MacdController',
				[
						'$http',
						'$scope',
						'ohlcService',
						'macdService',
						function MacdController($http, $scope, ohlcService, macdService) {

							this.echelle = "15";

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