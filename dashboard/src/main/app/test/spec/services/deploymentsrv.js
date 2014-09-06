'use strict';

describe('Service: Deploymentsrv', function () {

  // load the service's module
  beforeEach(module('adminDashboardApp'));

  // instantiate service
  var Deploymentsrv;
  beforeEach(inject(function (_Deploymentsrv_) {
    Deploymentsrv = _Deploymentsrv_;
  }));

  it('should do something', function () {
    expect(!!Deploymentsrv).toBe(true);
  });

});
