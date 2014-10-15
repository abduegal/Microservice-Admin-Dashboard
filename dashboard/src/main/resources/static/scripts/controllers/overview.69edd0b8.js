'use strict';

/**
 * @ngdoc function
 * @name adminDashboardApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the adminDashboardApp
 */
angular.module('adminDashboardApp')
  .controller('OverviewCtrl', function ($scope, OverviewSrv, DeploymentSrv, GraphSrv) {

    $scope.model = {};
    $scope.currentnamespace;

    $scope.init = function(){
      DeploymentSrv.getNameSpaces().then(
        function success(data){
          $scope.model.namespaces = data;
          $scope.currentnamespace = data[0];
          OverviewSrv.setNamespace($scope.currentnamespace);

          $scope.reload();
        }
      );
    };

    $scope.reload = function(){
      OverviewSrv.findServices().then(
        function result(result) {
          $scope.model.services = result;
          GraphSrv.loadGraph(result, $scope.updateModel);
        }
      );
    };

    $scope.updateModel = function(data){
      $scope.model.data = data;
      $scope.doHealthCheck(data);
      $scope.doMetricCheck(data);
      $scope.doPing(data);
    }

    function isHealthy(healthDetailsAsString){
    	return healthDetailsAsString.indexOf('"healthy": false')==-1;
    }
    
    function stringify(response){
    	return JSON.stringify(response.healthCheckResponse,null,2);
    }
    
    $scope.doHealthCheck = function(data){
        $scope.model.healthcheck = undefined;
        OverviewSrv.getHealthcheck(data.data).then(
            function success(response){
                var healthDetailsAsString = stringify(response);
                $scope.model.data.healthcheckDetails = healthDetailsAsString;
                $scope.model.data.healthcheck = isHealthy(healthDetailsAsString);
            }
        );
    }

    $scope.allHealthChecks = function(){
        $scope.healtchecksToggled = true;
        _.each($scope.model.services, function(service) {
            OverviewSrv.getHealthcheck(service.data).then(
                function success(response){
                	var healthDetailsAsString = stringify(response);
                	
                	var colorCode;
                	if(isHealthy(healthDetailsAsString)){
                    	 colorCode = '#449d44'; //green
                    }else{
                    	colorCode = '#c81f08'; //red
                    }
                    $('#' + service.data.instanceId + ' circle').css("fill", colorCode);
                }
            );
        })
    }

    $scope.undoHealtChecksColors = function(){
      var fill = d3.scale.category10();

      $scope.healtchecksToggled = false;
      _.each($scope.model.services, function(service) {
        $('#' + service.data.instanceId + ' circle').css("fill", fill(service.group));
      })
    }

    $scope.doMetricCheck = function(data){
      OverviewSrv.getMetrics(data.data).then(

        function success(data){
          $scope.model.data.metrics = data;
          $scope.model.data.gauges = $scope.buildGauges(data);
          $scope.model.data.timers = $scope.replaceDotsInKeys(data.timers);
          $scope.model.data.counters = $scope.replaceDotsInKeys(data.counters);
          $scope.model.data.meters = $scope.replaceDotsInKeys(data.meters);

        }
      );
    };

    $scope.doPing = function(data){
      OverviewSrv.getPing(data.data).then(
        function success(data){
          $scope.model.data.ping = data && data.indexOf("pong") !== -1;
        }
      );
    };

    $scope.setCurrentNamespace = function(namespace){
      $scope.currentnamespace = namespace;
      $scope.model.data = {};
      OverviewSrv.setNamespace(namespace);
      $('#chart').empty();
      $scope.reload();
    }

    $scope.buildGauges = function(data){
      var gauges = {};
      _.each(data.gauges, function(value, key){
        var splitted = key.split('.');
        var popped = splitted.pop();
        var newKey = splitted.join(' ');
        if(!gauges[newKey]) {
          gauges[newKey] = {};
        }
        gauges[newKey][popped] = value;
      });
      return gauges;
    }

    $scope.replaceDotsInKeys = function(metric){
      var result = {};
      _.each(metric, function(value, key){
        var newKey = key.split('.').join(' ');
        result[newKey] = value;
      });
      return result;
    }

  });
