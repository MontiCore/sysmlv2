package Requirement_5 {
  part def Vehicle {
    attribute mass: boolean;
  }

  part vehicle: Vehicle;

  requirement requirementUsage {
    // Setting primitive type as subject.
    subject mass = vehicle.mass;
  }
}
