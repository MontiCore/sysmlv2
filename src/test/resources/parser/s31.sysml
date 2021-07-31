part tank : FuelTankAssembly {
  port redefines fuelTankPort {
    out item redefines fuelSupply;
    in item redefines fuelReturn;
  }

  bind fuelTankPort::fuelSupply = pump::pumpOut;
  bind fuelTankPort::fuelReturn = tank::fuelIn;

  part pump : FuelPump {
    out item pumpOut : Fuel;
    in item pumpIn : Fuel;
  }
  part tank : FuelTank {
    out item fuelOut : Fuel;
    in item fuelIn : Fuel;
  }
}