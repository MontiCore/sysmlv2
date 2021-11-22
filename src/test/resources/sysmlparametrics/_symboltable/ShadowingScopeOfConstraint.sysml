package ShadowingScopeOfConstraint {
  attribute vehicleMass;

  constraint def VehicleMaxMass {
    // this vehicleMass should shadow vehicleMass defined outside this scope
    attribute vehicleMass;
    vehicleMass <= 100
  }

  constraint vehicleMaxMass {
    // this vehicleMass should shadow vehicleMass defined outside this scope
    attribute vehicleMass;
    vehicleMass <= 100
  }
}
