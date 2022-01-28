package Requirement_11 {
  part def Vehicle;
  part def Car:> Vehicle;

  part def Engine;
  part def TurboEngine :> Engine;

  part vehicle1: Vehicle; part car1: Car;
  part engine1: Engine; part turboEngine1: TurboEngine;

  // simple parameterized requirement
  requirement def ReqDef1(vehicle: Vehicle);
  requirement def ReqDef2(engine: Engine);
  requirement def ReqDef3(vehicle: Vehicle, engine: Engine);

  // req. usage with no type binding and params
  requirement reqUsage1(vehicle: Vehicle);

  // req. usage with type binding and no new params
  requirement reqUsage2: ReqDef1;

  // req. usage with type binding and constrained params
  requirement reqUsage3: ReqDef3(vehicle: Car, engine=turboEngine1);

  // req. usage with type binding and new params
  requirement reqUsage4: ReqDef1(engine: TurboEngine = turboEngine1);

  // req. usage with type binding and constrained params + new params
  requirement reqUsage5: ReqDef1(vehicle: Car, engine: TurboEngine = turboEngine1);

  // req. usage with single subsetting and no new params
  requirement reqUsage6 :> reqUsage1;

  // req. usage with single subsetting and constrained params
  requirement reqUsage7 :> reqUsage1(vehicle: Car);

  // req. usage with single subsetting and new params
  requirement reqUsage8 :> reqUsage1(engine: Engine);

  // req. usage with single subsetting and constrained params + new params
  requirement reqUsage9 :> reqUsage1(vehicle: Car = car1, engine=turboEngine1, mass: double);

  // req. usage with multiple subsetting, no overlap, and no new params
  requirement reqUsage10: ReqDef2;
  requirement reqUsage11:> reqUsage1, reqUsage10;

  // req. usage with multiple subsetting, no overlap, and constrained params
  requirement reqUsage12:> reqUsage1, reqUsage10(vehicle: Car);

  // req. usage with multiple subsetting, no overlap, and new params
  requirement reqUsage13:> reqUsage1, reqUsage10(mass: double = 100.0);

  // req. usage with multiple subsetting, no overlap, and constrained params + new params
  requirement reqUsage14:> reqUsage1, reqUsage10(vehicle=car1, engine: TurboEngine, mass=100.0);

  // req. usage with multiple subsetting, with overlap, and no new params
  requirement reqUsage15: ReqDef3;
  requirement reqUsage16:> reqUsage1, reqUsage15;

  // req. usage with multiple subsetting, with overlap, and constrained params
  requirement reqUsage17:> reqUsage1, reqUsage15(vehicle: Car);

  // req. usage with multiple subsetting, with overlap, and new params
  requirement reqUsage18:> reqUsage1, reqUsage15(mass: double = 100.0);

  // req. usage with multiple subsetting, with overlap, and constrained params + new params
  requirement reqUsage19:> reqUsage1, reqUsage15(vehicle=car1, engine: TurboEngine, mass=100.0);

  // req. usage with subsetting & type binding, no overlap, and no new params
  requirement reqUsage20: ReqDef1 :> reqUsage10;

  // req. usage with subsetting & type binding, no overlap, and constrained params
  requirement reqUsage21: ReqDef1 :> reqUsage10(vehicle: Car, engine: TurboEngine);

  // req. usage with subsetting & type binding, no overlap, and new params
  requirement reqUsage22: ReqDef1 :> reqUsage10(mass: double);

  // req. usage with subsetting & type binding, no overlap, and constrained params + new params
  requirement reqUsage23: ReqDef1 :> reqUsage10(vehicle: Car, engine: TurboEngine, mass=100.0);

  // req. usage with subsetting & type binding, with overlap, and no new params
  requirement reqUsage24: ReqDef1 :> reqUsage15;

  // req. usage with subsetting & type binding, with overlap, and constrained params
  requirement reqUsage25: ReqDef1 :> reqUsage15(vehicle: Car);

  // req. usage with subsetting & type binding, with overlap, and new params
  requirement reqUsage26: ReqDef1 :> reqUsage15(mass: double = 100.0);

  // req. usage with subsetting & type binding, with overlap, and constrained params + new params
  requirement reqUsage27: ReqDef1 :> reqUsage15(vehicle=car1, engine: TurboEngine, mass=100.0);

  // reset inherited assigned value
  requirement reqUsage28: ReqDef1(vehicle = vehicle1);
  requirement reqUsage29 :> reqUsage28(vehicle: Car);

}
