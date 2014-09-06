'use strict';

describe('Controller: DeploymentCtrl', function () {

  // load the controller's module
  beforeEach(module('adminDashboardApp'));

  var DeploymentCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DeploymentCtrl = $controller('DeploymentCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
