'use strict';

/**
 * @ngdoc overview
 * @name adminDashboardApp
 * @description
 * # adminDashboardApp
 *
 * Main module of the application.
 */
angular
  .module('adminDashboardApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'restangular',
    'ngTagsInput'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/overview', {
        templateUrl: 'views/overview.html',
        controller: 'OverviewCtrl'
      })
      .when('/details', {
        templateUrl: 'views/details.html',
        controller: 'DetailsCtrl'
      })
      .when('/details/:ns/:service_uuid', {
        templateUrl: 'views/details.html',
        controller: 'DetailsCtrl'
      })
      .when('/logs/', {
        templateUrl: 'views/logs.html',
        controller: 'LogsCtrl'
      })
      .when('/logs/:ns/:service_uuid', {
        templateUrl: 'views/logs.html',
        controller: 'LogsCtrl'
      })
      .otherwise({
        redirectTo: '/overview'
      });
  }).config(function(RestangularProvider) {
    RestangularProvider.setBaseUrl('/api');
  });
