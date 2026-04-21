part def Vehicle {
  attribute mass :> ISQ::mass;
  attribute dryMass;
  attribute cargoMass;
  attribute electricalPower;

  port pwrCmdPort;
  port vehicleToRoadPort;

  perform action providePower;
  perform action provideBraking;

  exhibit vehicleStates;
}
