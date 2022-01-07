package Requirement_8 {

  part def Vehicle;
  part def Car :> Vehicle;

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
}
