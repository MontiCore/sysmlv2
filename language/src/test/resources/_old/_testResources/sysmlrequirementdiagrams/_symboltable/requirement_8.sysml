package Requirement_8 {

  /*
    Requirements have been reordered to ensure order of elements does not
    have an effect on the implementation.
  */

  part def Vehicle;
  part def Car :> Vehicle;
  part def FlyingCar :> Car;

  requirement def RequirementWithSubject {
    subject vehicle: Vehicle;
  }

  // Requirement group with not defined subject,
  // with nested requirement that also doesn't define a subject.
  requirement requirementGroup {
    requirement nestedRequirement1InGroup;
  }

  requirement def RequirementWithoutSubjectWithNestedRequirements {
    require requirementGroup;
  }

  requirement def RequirementWithSubjectWithNestedRequirements {
    subject vehicle: Car;
    require requirementGroup;
  }

  requirement def ReqDef1 {
    subject vehicle: Vehicle;
  }

  requirement def ReqDef2 {
    subject car: Car;
  }

  requirement def ReqDef3:>ReqDef1, ReqDef2, ReqDef4;
  requirement def ReqDef4 {
    subject vehicle: FlyingCar;
  }

}
