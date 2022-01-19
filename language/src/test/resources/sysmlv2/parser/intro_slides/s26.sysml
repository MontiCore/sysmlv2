port def FuelPort {
  attribute temperature : Temp;
  out item fuelSupply : Fuel;
  in item fuelReturn : Fuel;
}

part def FuelTankAssembly {
  port fuelTankPort : FuelPort;
}

part def Engine {
  port engineFuelPort : ~FuelPort;
}
