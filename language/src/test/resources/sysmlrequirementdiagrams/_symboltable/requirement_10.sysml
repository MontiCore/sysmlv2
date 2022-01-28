package Requirement_10 {
  part def Vehicle;
  part def Car:> Vehicle;

  part def Engine;
  part def TurboEngine :> Engine;

  part vehicle1: Vehicle; part car1: Car;
  part engine1: Engine; part turboEngine1: TurboEngine;

  // simple parameterized requirement
  requirement def ReqDef1(vehicle: Vehicle);

  // specialized req., single inheritance with no new/redefined params
  requirement def ReqDef2 :> ReqDef1;

  // specialized req., single inheritance with redefined but same params
  requirement def ReqDef3 :> ReqDef1(vehicle: Vehicle);

  // specialized req., single inheritance with constrained param
  requirement def ReqDef4 :> ReqDef1(vehicle: Car);

  // specialized req., inheriting specialized req. with no new/redefined params
  requirement def ReqDef5 :> ReqDef4;

  // specialized req., single inheritance with new param
  requirement def ReqDef6 :> ReqDef1(engine: Engine);

  // specialized req., single inheritance with assigned value
  requirement def ReqDef7 :> ReqDef1(vehicle=vehicle1);

  // specialized req., single inheritance with compatible assigned value
  requirement def ReqDef8 :> ReqDef1(vehicle=car1);

  // specialized req., single inheritance with constrained param and assigned value
  requirement def ReqDef9 :> ReqDef1(vehicle: Car = car1);

  // specialized req., single inheritance with same param definition and compatible assigned value
  requirement def ReqDef10 :> ReqDef1(vehicle: Vehicle = car1);

  // specialized req., single inheritance with constrained param and compatible assigned value
  requirement def ReqDef11 :> ReqDef1(vehicle: Car = car1);

  // specialized req., single inheritance with with param definition and assigned value
  requirement def ReqDef12 :> ReqDef1(vehicle: Vehicle = vehicle1);

  // specialized req., multiple inheritance without overlapping parameters, no new params
  requirement def ReqDef13(engine: Engine);
  requirement def ReqDef14 :> ReqDef1, ReqDef13;

  // specialized req., multiple inheritance without overlapping parameters, constrained params
  requirement def ReqDef15 :> ReqDef1, ReqDef13(vehicle: Car, engine=turboEngine1);

  // specialized req., multiple inheritance without overlapping parameters, new params
  requirement def ReqDef16 :> ReqDef1, ReqDef13(mass:double = 100.0);

  // specialized req., multiple inheritance without overlapping parameters, constrained params + new params
  requirement def ReqDef17 :> ReqDef1, ReqDef13(vehicle=car1, engine: TurboEngine, mass = 100.0);

  // specialized req., multiple inheritance with overlapping parameters, no new params
  requirement def ReqDef18(vehicle: Vehicle, engine: Engine);
  requirement def ReqDef19 :> ReqDef1, ReqDef18;

  // specialized req., multiple inheritance with overlapping parameters, constrained params
  requirement def ReqDef20 :> ReqDef1, ReqDef18(vehicle:Car, engine=turboEngine1);

  // specialized req., multiple inheritance with overlapping parameters, new params
  requirement def ReqDef21 :> ReqDef1, ReqDef18(mass = 100.0);

  // specialized req., multiple inheritance with overlapping parameters, constrained params + new params
  requirement def ReqDef22 :> ReqDef1, ReqDef18(vehicle=car1, engine: TurboEngine, mass = 100.0);

  // reset inherited assigned value
  requirement def ReqDef23(vehicle: Vehicle=vehicle1);
  requirement def ReqDef24 :> ReqDef23(vehicle: Car);
  requirement def ReqDef25 :> ReqDef23(vehicle: Car = car1);

  // assigned values are not inherited, if param. is redefined (even without type)
  requirement def ReqDef26 :> ReqDef7(vehicle: Vehicle);
  requirement def ReqDef27 :> ReqDef7(vehicle: Vehicle = vehicle1);
  requirement def ReqDef28 :> ReqDef7(vehicle: Vehicle = car1);
  requirement def ReqDef29 :> ReqDef7(vehicle);

  // type is properly propagated across the inheritance chain
  requirement def ReqDef30(vehicle);
  requirement def ReqDef31 :> ReqDef30(vehicle: Vehicle);
  requirement def ReqDef32 :> ReqDef30(vehicle = car1);
  requirement def ReqDef33 :> ReqDef31(vehicle);
  requirement def ReqDef34 :> ReqDef32(vehicle);

}
