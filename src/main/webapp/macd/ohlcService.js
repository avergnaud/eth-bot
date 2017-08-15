angular.module('ohlcServiceModule', [])
	.factory('ohlcService', ['$http',
		function($http) {

			var options = {
				chart : {
					type : 'candlestickBarChart',
					height : 450,
					margin : {
						top : 20,
						right : 20,
						bottom : 66,
						left : 60
					},
					x : function(d) {
						return d['date'];
					},
					y : function(d) {
						return d['close'];
					},
					xAxis : {
						axisLabel : 'Dates',
						tickFormat : function(d) {
							return d3.time.format('%x')(new Date(d * 1000));
						},
						showMaxMin : false
					},

					yAxis : {
						axisLabel : 'OHLC',
						tickFormat : function(d) {
							return '$' + d3.format(',.1f')(d);
						},
						showMaxMin : false
					},
					zoom : {
						enabled : false,
						horizontalOff : false,
						verticalOff : true
					},
					useInteractiveGuideline : true
				}
			};

			var refresh = function(echelle) {
				var url = "/OHLC?grain=" + echelle;
				return $http.get(url);;
			};

			return {
				options : options,
				refresh : refresh
			};
		}] );