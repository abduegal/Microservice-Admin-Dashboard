'use strict';

describe('Controller: ServiceconfigCtrl', function () {

  // load the controller's module
  beforeEach(module('adminDashboardApp'));

  var ServiceconfigCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ServiceconfigCtrl = $controller('ServiceconfigCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
