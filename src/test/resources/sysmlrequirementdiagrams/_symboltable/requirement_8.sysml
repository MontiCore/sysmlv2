package Requirement_8 {

  part def Vehicle;
  part def Car :> Vehicle;

  requirement def RequirementWithSubject {
    subject vehicle: Vehicle;
  }

  // Requirement group with not defined subject,
  // with nested requirement requiring a subject of type 'Vehicle'.
  requirement requirementGroup {
    requirement nestedRequirement1InGroup;
    requirement nestedRequirement2InGroup: RequirementWithSubject;
  }

  requirement def RequirementWithoutSubjectWithNestedRequirements {
    require requirementGroup;
  }

  requirement def RequirementWithSubjectWithNestedRequirements {
    subject vehicle: Car;
    require requirementGroup;
  }
}