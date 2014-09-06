'use strict';

/**
 * @ngdoc service
 * @name adminDashboardApp.Deploymentsrv
 * @description
 * # Deploymentsrv
 * Service in the adminDashboardApp.
 */
angular.module('adminDashboardApp')
  .service('DeploymentSrv', function DeploymentSrv(Restangular) {
    var deployment = Restangular.all('deployment');

    this.getNameSpaces = function() {
      return deployment.one('namespaces').getList();
    };

    this.getFilesInDeployDirectory = function() {
      return deployment.one('files').getList();
    };


  });
