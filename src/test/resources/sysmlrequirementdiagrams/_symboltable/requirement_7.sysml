package Requirement_7 {

  part def Vehicle {
    attribute mass: float;
  }

  part def Car :> Vehicle;
  part def FlyingCar :> Car;

  // Requirement definition with specific subject
  requirement def RequirementWithSubject {
    subject vehicle: Vehicle;
    attribute maxMass: float;
  }

  // Specialized requirement definition with inherited subject
  requirement def RequirementWithSubjectSpecialized :> RequirementWithSubject;

  // Specialized requirement definition that redefines inherited subject
  requirement def SpecializedRequirementWithRedefinedSubject :> RequirementWithSubject {
    subject vehicle: Car;
  }

  // Requirement usage with predefined subject given by definition
  requirement requirementWithSubject: RequirementWithSubject;

  // Requirement usage with predefined subject (redefined in definition)
  requirement requirementWithoutSubject: SpecializedRequirementWithRedefinedSubject;

  // Requirement usage with redefined subject
  requirement requirementWithSubject: SpecializedRequirementWithRedefinedSubject {
    subject vehicle: FlyingCar;
  }
}