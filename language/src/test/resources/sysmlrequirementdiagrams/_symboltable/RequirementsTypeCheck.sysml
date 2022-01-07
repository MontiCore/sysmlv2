package Requirements {

  part def Bolt;

  part def Engine {
    part bolt: Bolt;
  }

  part def Vehicle {
    part engine: Engine;
    attribute actual_mass: float;
  }

  part def Car :> Vehicle {

  }

  part def Brand {
    attribute ll: Real;
  }
  part def Vintage :> Car, Brand {

  }

  part car: Vintage;

  requirement carRequirement {
    subject engine = car.engine;
  }

  requirement def VehicleRequirement {
    subject vehicle: Vehicle;

    requirement boltRequirement {
      subject bolt = vehicle.engine.bolt;

      require constraint c1 {
        bolt == "copper"
      }
    }

    require constraint c1 {
      vehicle.actual_mass < 1000
    }
  }
}