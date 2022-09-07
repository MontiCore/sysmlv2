package Requirement_1 {
  part def Bolt {
    attribute diameter: Real;
  }

  part def Engine {
    part bolt: Bolt;
  }

  part def Vehicle {
    part engine: Engine;
  }

  // Requirement group with nested requirements whose subjects are fields of the main subject.
  requirement requirementGroup {
    subject vehicle: Vehicle;

    requirement nestedRequirement_1 {
      subject engine = vehicle.engine;
    }

    requirement nestedRequirement_2 {
      subject bolt = vehicle.engine.bolt;
    }
  }

  part vehicle: Vehicle;
  part engine: Engine;
  part def Car:>Vehicle;
  part car: Car;
  satisfy requirementGroup by car;

}
