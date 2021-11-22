package Requirement_4 {
  part def Vehicle {
    part engine {
      attribute mass: Real;
    }
  }

  part vehicle: Vehicle;

  requirement requirementGroup {
    subject vehicle: Vehicle;

    requirement nestedRequirement {
      subject mass = vehicle.engine;
    }
  }
}
