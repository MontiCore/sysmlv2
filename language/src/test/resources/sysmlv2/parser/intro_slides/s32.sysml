part tank : FuelTankAssembly {
  port redefines fuelTankPort {
    out item redefines fuelSupply;
    in item redefines fuelReturn;
 }

  part pump : FuelPump {
    out item pumpOut : Fuel = fuelTankPort::fuelSupply;
    in item pumpIn : Fuel;
  }
  part tank : FuelTank {
    out item fuelOut : Fuel;
    in item fuelIn : Fuel = fuelTankPort::fuelReturn;
  }
}
