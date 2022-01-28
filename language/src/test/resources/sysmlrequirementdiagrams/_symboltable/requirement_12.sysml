package Requirement_12 {
  part def Vehicle;
  part def Car:> Vehicle;
  part vehicle1: Vehicle;
  part car1: Car;

  requirement def DuplicateParamsInDef(vehicle: Vehicle, vehicle: Vehicle);

  requirement def SimpleRequirementDef(vehicle: Car);
  requirement def SimpleRequirementDef2(vehicle: Vehicle);

  requirement def SpecializedRequirementWithInvalidParameterConstraint
    :> SimpleRequirementDef(vehicle: Vehicle);

  requirement def SpecializedRequirementWithInvalidInheritedParams
    :> SimpleRequirementDef, SimpleRequirementDef2;

  requirement def RequirementDefWithInvalidParamValue(mass: double = 10);

  requirement duplicateParamsInUsage(vehicle: Vehicle, vehicle: Vehicle);

  requirement requirementUsageWithInvalidParamValue(vehicle: Car = 100.0);

  requirement requirementUsageWithInvalidParameterConstraint
      : SimpleRequirementDef(vehicle: Vehicle);

  requirement simpleRequirementUsage(vehicle: Vehicle);

  requirement requirementUsageWithInvalidInheritedParams
      : SimpleRequirementDef
      :> simpleRequirementUsage(vehicle: Vehicle);

  requirement def ReqDefWithAssignedValue(vehicle=vehicle1);
  requirement def ReqDefWithInvalidAssignedValue(vehicle: Car = vehicle1);

  requirement reqUsageWithAssignedValue(vehicle=vehicle1);
  requirement reqUsageWithInvalidAssignedValue(vehicle: Car = vehicle1);
}
