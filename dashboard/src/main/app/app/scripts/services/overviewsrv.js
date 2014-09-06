'use strict';

/**
 * @ngdoc service
 * @name adminDashboardApp.Overviewsrv
 * @description
 * # Overviewsrv
 * Service in the adminDashboardApp.
 */
angular.module('adminDashboardApp')
  .service('OverviewSrv', function OverviewSrv(Restangular) {
     var overview = Restangular.all('overview');

     this.findServices = function(){
       return overview.one('findservices').getList();
     };

     this.getHealthcheck = function(serviceinstance){
       return overview.all('healthcheck').post(serviceinstance);
     };

     this.getMetrics = function(serviceinstance){
       return overview.all('metrics').post(serviceinstance);
     };

     this.getPing = function(serviceinstance){
       return overview.all('ping').post(serviceinstance);
     };

     this.loadLogs = function(serviceinstance, lines){
       return overview.all('logfile').all(lines).post(serviceinstance);
     };

     this.setNamespace = function(namespace){
       overview = Restangular.all('overview').all(namespace);
     };

  });
