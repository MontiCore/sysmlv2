/* (c) https://github.com/MontiCore/monticore */
package 'Symbol Definition' {
  block Vehicle {
      value mass : Real;
      value status : VehicleStatus;

      part eng : Engine;

      ref driver : Person;
    }
}
