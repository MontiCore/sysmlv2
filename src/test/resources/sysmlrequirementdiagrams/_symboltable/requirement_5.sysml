package Requirement_5 {
  part def Vehicle {
    attribute mass: boolean;
  }

  part vehicle: Vehicle;

  requirement requirementGroup {
    // Setting primitive type as subject.
    subject mass = vehicle.mass;
  }
}