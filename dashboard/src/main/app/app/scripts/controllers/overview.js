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

    $scope.doHealthCheck = function(data){
        $scope.model.healthcheck = undefined;
        OverviewSrv.getHealthcheck(data.data).then(
            function success(response){
                var stringify = JSON.stringify(response.healthCheckResponse,null,2);
                $scope.model.data.healthcheckDetails = stringify;
                $scope.model.data.healthcheck = stringify.indexOf('"healthy": false')>0;
            }
        );
    }

    $scope.allHealthChecks = function(){
        $scope.healtchecksToggled = true;
        _.each($scope.model.services, function(service) {
            OverviewSrv.getHealthcheck(service.data).then(
                function success(data){
                    var stringify = JSON.stringify(data,null,2);
                    if(stringify.indexOf('"healthy": false')>0){
                        $('#' + service.data.instanceId + ' circle').css("fill", '#c81f08');
                    }else{
                        $('#' + service.data.instanceId + ' circle').css("fill", '#449d44');
                    }
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
