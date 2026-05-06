/* (c) https://github.com/MontiCore/monticore */
package FlowConnectionInterfaceExample {
  import PortExample::*;

  part def Vehicle;

  interface def FuelInterface {
    end supplierPort : FuelOutPort;
    end consumerPort : FuelInPort;

    flow supplierPort.fuelSupply to consumerPort.fuelSupply;
    flow consumerPort.fuelReturn to supplierPort.fuelReturn;
  }

  part vehicle : Vehicle {
    part tankAssy : FuelTankAssembly;
    part eng : Engine;

    interface : FuelInterface connect
      tankAssy.fuelTankPort to
      eng.engineFuelPort;
  }
}
