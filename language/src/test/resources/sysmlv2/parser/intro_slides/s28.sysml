part vehicle : Vehicle {
  part tankAssy : FuelTankAssembly {
    port redefines fuelTankPort;
  }
  part eng : Engine {
    port redefines engineFuelPort;
  }

  interface : FuelInterface connect
    supplierPort => tankAssy::fuelTankPort to
    consumerPort => eng::engineFuelPort;
}
