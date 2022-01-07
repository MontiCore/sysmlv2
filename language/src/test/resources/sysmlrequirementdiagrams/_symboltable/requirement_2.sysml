package Requirement_2 {
  // Part definition created with ad-hoc part usages.
  part def Vehicle {
    part engine {
       part bolt {
          attribute diameter: Real;
       }
    }
  }

  requirement requirementGroup {
    // Subject defined by a type.
    subject vehicle: Vehicle;

    // Requirement usage setting main subjects' field as its own subject.
    requirement nestedRequirement_1 {
      subject engine = vehicle.engine;
    }

    // Requirement usage setting main subjects' field as its own subject.
    requirement nestedRequirement_2 {
      subject bolt = vehicle.engine.bolt;
    }
  }
}