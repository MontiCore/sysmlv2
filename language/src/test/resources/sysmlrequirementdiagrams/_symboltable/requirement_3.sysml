package Requirement_3 {
  part def Bolt {
    attribute diameter: Real;
  }

  part def Engine {
    part bolt: Bolt;
  }

  part def Vehicle {
    part engine: Engine;
  }

  part vehicle1: Vehicle;

  requirement requirementGroup {
    // Subject bound to existing part usage.
    subject vehicle = vehicle1;

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