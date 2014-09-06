'use strict';

/**
 * @ngdoc function
 * @name adminDashboardApp.controller:DeploymentCtrl
 * @description
 * # DeploymentCtrl
 * Controller of the adminDashboardApp
 */
angular.module('adminDashboardApp')
  .controller('MainCtrl', function ($scope, SystemSrv) {

    SystemSrv.getSystemInformation().then(
      function success(data) {
        $scope.systemInfo = data;
      }
    );

    $scope.toMB = function(bytes){
      return (bytes / 1024 / 1024).toFixed(0);
    }

    $scope.pages = [
      {name: 'Overview', href: 'overview', active: true},
      {name: 'Details', href: 'details'},
      {name: 'Logs', href: 'logs'}
    ];

    $scope.$on('$routeChangeSuccess', function(event, current, previous){
      _.each($scope.pages, function(page){
        page.active = false;
        if (current && current.$$route.originalPath.indexOf(page.href) !== -1 ){
          page.active = true;
        }
      });


    });



  });


