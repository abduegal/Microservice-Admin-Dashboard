'use strict';

/**
 * @ngdoc function
 * @name adminDashboardApp.controller:DeploymentCtrl
 * @description
 * # DeploymentCtrl
 * Controller of the adminDashboardApp
 */
angular.module('adminDashboardApp')
  .controller('DetailsCtrl', function ($scope, DeploymentSrv, OverviewSrv, $routeParams, $location) {

    $scope.selectedService = $routeParams.service_uuid;
    $scope.model = {};
    $scope.currentnamespace = $routeParams.ns;
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
          $scope.loadHystrix();
        },
        function error(){
          $scope.model.services = {};
        }
      );
    };

    $scope.selectService = function(service) {
      $location.path('details/' + $scope.currentnamespace + '/' + service.id);
      if(source) {
        source.close();
      }
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

    $scope.loadHystrix = function() {
      if($scope.selectedService) {
        $('.dependencies').empty();
        var service = _.find($scope.model.services, {'id': $scope.selectedService});

        var hystrixMonitor = new HystrixCommandMonitor('dependencies', {includeDetailIcon:false});

        source = new EventSource('http://'+service.data.listenAddress +':'+ service.data.listenPort +'/tenacity/metrics.stream');

        source.addEventListener('message', hystrixMonitor.eventSourceMessageListener, false);

      }
    };


  });
