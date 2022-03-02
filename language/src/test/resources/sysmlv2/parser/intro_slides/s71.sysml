requirement <"1.1"> fullVehicleMassLimit : VehicleMassLimitationRequirement {
  subject vehicle : Vehicle;
  attribute myMassReqd :>> massReqd = 2000;

  assume constraint {
    doc /* Fuel tank is full. */
    vehicle::fuelMass == vehicle::fuelFullMass
  }
}

requirement <"1.2"> emptyVehicleMassLimit : VehicleMassLimitationRequirement {
  subject vehicle : Vehicle;
  attribute myMassReqd :>> massReqd = 1500;

  assume constraint {
    doc /* Fuel tank is empty. */
    vehicle::fuelMass == 0
  }
}
