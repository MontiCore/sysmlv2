package Requirement_7 {

  /*
    Requirements have been reordered to ensure order of elements does not
    have an effect on the implementation.
  */

  part def Vehicle {
    attribute mass: float;
  }

  part def Car :> Vehicle;
  part def FlyingCar :> Car;

  // Specialized requirement definition that redefines inherited subject
  requirement def SpecializedRequirementWithRedefinedSubject :> RequirementWithSubject {
    subject vehicle: Car;
  }

  // Specialized requirement definition with inherited subject
  requirement def SpecializedRequirementWithSubject :> RequirementWithSubject;

  // Requirement definition with specific subject
  requirement def RequirementWithSubject {
    subject vehicle: Vehicle;
    attribute maxMass: float;
  }

  // Requirement usage with predefined subject (redefined in definition)
  requirement requirementWithSubject2: SpecializedRequirementWithRedefinedSubject;

  // Requirement usage with redefined subject
  requirement requirementWithSpecializedSubject: SpecializedRequirementWithRedefinedSubject {
    subject vehicle: FlyingCar;
  }

  // Requirement usage with predefined subject given by definition
  requirement requirementWithSubject1: RequirementWithSubject;

}
