package Requirement_11 {
  part def Vehicle;
  part def Car:>Vehicle;

  part vehicle: Vehicle;
  part car: Car;

  requirement def ReqDefWithNoParams;
  requirement def ReqDefWithParams(a1: Vehicle, a2);

  // requirement usage with no parameters
  requirement reqUsage1;

  // requirement usage with its own parameters
  requirement reqUsage2(a1: Vehicle, a2: Vehicle = vehicle, a3: Vehicle = car);

  // requirement usage with type binding and no parameters
  requirement reqUsage3: ReqDefWithNoParams;

  // requirement usage with type binding and its own parameters
  requirement reqUsage4: ReqDefWithNoParams(a1: Car = car);

  // requirement usage with type binding with partially redefined parameters
  requirement reqUsage5: ReqDefWithParams(a1: Vehicle);

  // requirement usage with type binding and fully redefined parameters
  requirement reqUsage6: ReqDefWithParams(a1: Vehicle, a2: Vehicle = vehicle);

  // requirement usage with type binding, fully redefined parameters, and its own parameters
  requirement reqUsage7: ReqDefWithParams(a1: Vehicle, a2: Vehicle = vehicle, a3: Car);

  // requirement usage with param that has value but no type
  requirement reqUsage8(a1 = vehicle);
  requirement reqUsage9 :> reqUsage8(a1: Car = car);

  // requirement usage with subsetting that has no parameters
  requirement reqUsage10 :> reqUsage1;

  // requirement usage with subsetting that has no parameters with its own new parameters
  requirement reqUsage11 :> reqUsage1(a1: Vehicle, a2);

  // requirement usage with subsetting that has parameters
  // params inherited
  requirement reqUsage12 :> reqUsage2;
  // partial redefinition
  requirement reqUsage13 :> reqUsage2(a1: Car = car, a2: Vehicle = vehicle);
  // complete redefinition
  requirement reqUsage14 :> reqUsage2(a1: Car = car, a2: Vehicle = vehicle, a3: Vehicle);
  // complete redefinition and new paras
  requirement reqUsage15 :> reqUsage2(a1: Car = car, a2: Vehicle = vehicle, a3: Vehicle, a4: Car);

  // requirement usage with type binding and subsetting
  // with no. params
  requirement reqUsage16
    : ReqDefWithNoParams
    :> reqUsage1;

  // with its own params
  requirement reqUsage17
    : ReqDefWithNoParams
    :> reqUsage1(a1 = vehicle, a2: Car);

  // with equal no. of params, no new params
  requirement reqUsage18(a1, a2: Car);
  requirement reqUsage19
    : ReqDefWithParams
    :> reqUsage18(a1: Vehicle = vehicle, a2: Car = car);

  // with equal no. of params and new params
  requirement reqUsage20
    : ReqDefWithParams
    :> reqUsage18(a1: Vehicle = vehicle, a2: Car = car, a3: Car, a4 = vehicle);

  // with different no. of params, no new params
  requirement reqUsage21
    : ReqDefWithParams
    :> reqUsage2(a1: Vehicle = vehicle, a2: Car = car, a3: Car);

  // with different no. of params and new params
  requirement reqUsage22
    : ReqDefWithParams
    :> reqUsage2(a1: Vehicle = vehicle, a2: Car = car, a3: Car, a4 = vehicle);
}
