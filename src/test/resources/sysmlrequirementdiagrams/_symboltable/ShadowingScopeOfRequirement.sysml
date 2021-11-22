package ShadowingScopeOfRequirement {
  attribute vehicleMaxMass;

  requirement def VehicleMaxMassRequirement {
    // this vehicleMaxMass should shadow vehicleMaxMass defined outside this scope
    attribute vehicleMaxMass;
  }

  requirement vehicleMaxMassRequirement {
    // this vehicleMaxMass should shadow vehicleMaxMass defined outside this scope
    attribute vehicleMaxMass;
  }
}
