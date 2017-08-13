var app = angular.module('macdControllerModule', ['nvd3']);

app.controller('MainCtrl', function($scope, $http) {
	  
	this.minutes = [1 , 5, 15, 30, 60, 240, 1440, 10080, 21600];
	this.selectedMinutes = 1;
	
	  $scope.options = {
	            chart: {
	                type: 'lineChart',
	                height: 450,
	                margin : {
	                    top: 20,
	                    right: 20,
	                    bottom: 40,
	                    left: 55
	                },
	                x: function(d){ return d.x; },
	                y: function(d){ return d.y; },
	                useInteractiveGuideline: true,
	                dispatch: {
	                    stateChange: function(e){ console.log("stateChange"); },
	                    changeState: function(e){ console.log("changeState"); },
	                    tooltipShow: function(e){ console.log("tooltipShow"); },
	                    tooltipHide: function(e){ console.log("tooltipHide"); }
	                },
	                xAxis: {
	                    axisLabel: 'Dates',
	                        tickFormat: function(d) {
	                            return d3.time.format('%c')(new Date(d));
	                        },
	                        showMaxMin: false
	                },
	                yAxis: {
	                    axisLabel: 'MACD et SIGNAL',
	                    tickFormat: function(d){
	                        return d3.format('.02f')(d);
	                    },
	                    axisLabelDistance: -10
	                },
	                callback: function(chart){
	                    console.log("!!! lineChart callback !!!");
	                }
	            },
	            title: {
	                enable: true,
	                text: 'Title for Line Chart'
	            },
	            subtitle: {
	                enable: true,
	                text: 'A bullish crossover occurs when the MACD turns up and crosses above the signal line. A bearish crossover occurs when the MACD turns down and crosses below the signal line.',
	                css: {
	                    'text-align': 'center',
	                    'margin': '10px 13px 0px 7px'
	                }
	            },
	            caption: {
	                enable: true,
	                html: 'caption.',
	                css: {
	                    'text-align': 'justify',
	                    'margin': '10px 13px 0px 7px'
	                }
	            }
	        };

	  this.refresh = function refresh() {
			var url = "/MACD?grain=" + this.selectedMinutes.toString();
			return $http.get(url).then(function(response) {
				$scope.data = response.data;
			});
		};

		this.refresh();
    
});
