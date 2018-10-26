angular.module('macdServiceModule', [])
	.factory('macdService', ['$http',
		function($http) {
		
		var options = {
				chart : {
					type : 'lineChart',
					height : 450,
					margin : {
						top : 20,
						right : 20,
						bottom : 40,
						left : 55
					},
					x : function(d) {
						return d.x;
					},
					y : function(d) {
						return d.y;
					},
					useInteractiveGuideline : true,
					dispatch : {
						stateChange : function(e) {
							console.log("stateChange");
						},
						changeState : function(e) {
							console.log("changeState");
						},
						tooltipShow : function(e) {
							console.log("tooltipShow");
						},
						tooltipHide : function(e) {
							console.log("tooltipHide");
						}
					},
					xAxis : {
						axisLabel : 'Dates',
						tickFormat : function(d) {
							return d3.time.format('%c')(
									new Date(d));
						},
						showMaxMin : false
					},
					yAxis : {
						axisLabel : 'MACD et SIGNAL',
						tickFormat : function(d) {
							return d3.format('.02f')(d);
						},
						axisLabelDistance : -10
					},
					callback : function(chart) {
						console
								.log("!!! lineChart callback !!!");
					}
				}
			};

		var refresh = function(echelle) {
			var url = "/MACD?grain=" + echelle;
			    if(window.location.pathname == "/market/macd/") {
			        url = "/market/MACD?grain=" + echelle;/* bad */
			    }
			return $http.get(url);;
		};

		return {
			options : options,
			refresh : refresh
		};
	
	}] );