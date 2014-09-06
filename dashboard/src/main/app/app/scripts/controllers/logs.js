'use strict';

/**
 * @ngdoc function
 * @name adminDashboardApp.controller:DeploymentCtrl
 * @description
 * # DeploymentCtrl
 * Controller of the adminDashboardApp
 */
angular.module('adminDashboardApp')
  .controller('LogsCtrl', function ($scope, DeploymentSrv, OverviewSrv, $routeParams, $location) {

    $scope.selectedService = $routeParams.service_uuid;
    $scope.model = {};
    $scope.currentnamespace = $routeParams.ns;
    $scope.lines = 100;
    var source;

    $scope.init = function(){
      DeploymentSrv.getNameSpaces().then(
        function success(data){
          $scope.model.namespaces = data;
          if(!$scope.currentnamespace) {
            $scope.currentnamespace = data[0];
          }
          OverviewSrv.setNamespace($scope.currentnamespace);

          $scope.reload();
        }
      );
    };

    $scope.reload = function(){
      OverviewSrv.findServices().then(
        function result(result) {
          $scope.model.services = result;
          $scope.loadLogs();
        },
        function error(){
          $scope.model.services = {};
        }
      );
    };

    $scope.selectService = function(service) {
      $location.path('logs/' + $scope.currentnamespace + '/' + service.id);
    };

    $scope.isSelected = function(service){
      return $scope.selectedService == service.id;
    }

    $scope.setCurrentNamespace = function(namespace){
      $scope.currentnamespace = namespace;
      $scope.model.data = {};
      OverviewSrv.setNamespace(namespace);
      $scope.reload();
    }

    $scope.isCurrentNamespace = function(namespace){
      return namespace === $scope.currentnamespace;
    }

    $scope.$watch('lines', function(){
      $scope.loadLogs();
    });

    $scope.loadLogs = function() {
      if($scope.selectedService) {
        var service = _.find($scope.model.services, {'id': $scope.selectedService});
        if(service){
          OverviewSrv.loadLogs(service.data, $scope.lines).then(
            function success(data) {
              $scope.logs = data.split('\n');
            }
          );
        }
      }
    };


  });
