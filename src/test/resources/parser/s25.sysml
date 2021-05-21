port def FuelOutPort {
  attribute temperature : Temp;
  out item fuelSupply : Fuel;
  in item fuelReturn : Fuel;
}

port def FuelInPort {
  value temperature : Temp;
  in item fuelSupply : Fuel;
  out item fuelReturn : Fuel;
}

part def FuelTankAssembly {
  port fuelTankPort : FuelOutPort;
}

part def Engine {
  port engineFuelPort : FuelInPort;
}
