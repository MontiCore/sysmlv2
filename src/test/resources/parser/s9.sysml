part def Vehicle {
  attribute mass : ScalarValues::Real;
  part eng : Engine;
  ref part driver : Person;
}

attribute def VehicleStatus {
  import ScalarValues::*;
  gearSetting : Integer;
  acceleratorPosition : Real;
}

part def Engine;
part def Person;
