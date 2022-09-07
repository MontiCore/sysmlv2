package Requirement_12 {
  part def Vehicle;
  part def Car :> Vehicle;
  part vehicle: Vehicle;

  requirement def ReqDef1(a: Vehicle);
  requirement def ReqDef2(a: Vehicle);
  requirement def ReqDef3(a: Vehicle, b: Vehicle, c: Car);
  requirement def ReqDef4 :> ReqDef3(a: Car);

  requirement reqUsageWithoutType;
  requirement reqUsageTyped: ReqDef1(a: Vehicle, b: Car);
  requirement reqUsageTyped2: ReqDef1(a: Vehicle);

  requirement def ReqDefWithFeatureValue(a: Vehicle = vehicle);
  requirement def ReqDefWithInvalidParamRedefinition :> ReqDef1(a: double);
  requirement def ReqDefWithNoMandatoryRedefinition :> ReqDef1, ReqDef2;
  requirement def ReqDefWithNonRedefinedInheritedParams :> ReqDef1, ReqDef4(a: Car);

  requirement reqUsageWithInvalidParamRedefinition1: ReqDef1(a: double);
  requirement reqUsageWithInvalidParamRedefinition2:> reqUsageTyped(a: double);
  requirement reqUsageWithNoMandatoryRedefinition1: ReqDef1 :> reqUsageWithoutType;
  requirement reqUsageWithNoMandatoryRedefinition2: ReqDef1 :> reqUsageTyped;
  requirement reqUsageWithMissingMandatoryRedefinition: ReqDef1 :> reqUsageTyped(a: Car);
  requirement reqUsageWithIncompatibleFeatureValue1: ReqDef1(a: Vehicle = 100.0);
  requirement reqUsageWithIncompatibleFeatureValue2:> reqUsageTyped(a: Car = vehicle);
  requirement reqUsageWithNonRedefinedInheritedParams1: ReqDef3 :> reqUsageTyped(a: Car = vehicle);
  requirement reqUsageWithNonRedefinedInheritedParams2 :> reqUsageTyped, reqUsageTyped2(a: Car = vehicle);

}
