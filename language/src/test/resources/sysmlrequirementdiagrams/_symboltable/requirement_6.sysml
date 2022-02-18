package Requirement_6 {

  /*
    Requirements have been reordered to ensure order of elements does not
    have an effect on the implementation.
  */

  part def Vehicle {
    attribute mass: float;
  }

  part def Car :> Vehicle;

  // Requirement usage with subject defined for the first time
  requirement requirementWithSubjectDefined: RequirementWithoutSubject {
    subject vehicle: Vehicle;
  }

  // Requirement usage with predefined subject
  requirement requirementWithSubject: SpecializedRequirementWithDefinedSubject;

  // Requirement usage with redefined subject
  requirement requirementWithSubjectRedefined: SpecializedRequirementWithDefinedSubject {
    subject vehicle: Car;
  }

  // Specialized requirement definition with no subject
  requirement def RequirementWithoutSubjectSpecialized :> RequirementWithoutSubject;

  // Specialized requirement definition that defines subject for the first time
  requirement def SpecializedRequirementWithDefinedSubject :> RequirementWithoutSubject {
    subject vehicle: Vehicle;
  }

  // Requirement definition with no body
  requirement def NoBodyRequirement;

  // Requirement definition with a body but without a specific subject
  requirement def RequirementWithoutSubject {
    attribute maxMass: float;
  }

  // Requirement usage without definition
  requirement noBodyRequirement;

  // Requirement usage with definition
  requirement requirementWithoutSubject: RequirementWithoutSubject;

}
