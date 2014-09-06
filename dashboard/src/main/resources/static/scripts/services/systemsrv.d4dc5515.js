'use strict';

/**
 * @ngdoc service
 * @name adminDashboardApp.Deploymentsrv
 * @description
 * # Deploymentsrv
 * Service in the adminDashboardApp.
 */
angular.module('adminDashboardApp')
  .service('SystemSrv', function DeploymentSrv(Restangular) {
    var system = Restangular.all('system');

    this.getSystemInformation = function() {
      return system.one('info').get();
    };

  });
