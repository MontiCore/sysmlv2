package Requirement_13 {

  part def FuelTank {
    attribute volume: double;
  }

  part def Vehicle {
    attribute mass: double;
    part fuelTank: FuelTank;
    part engine {
      attribute mass: double;
    }
  }

  part def Car:> Vehicle {
    attribute wheels: int = 4;
  }

  requirement def CarRequirement(maxMass: double, minMass: double) {
    subject car: Car;
    attribute minWheels: int = 2;
    attribute maxWheels: int = 8;

    // equals
    assume constraint { minMass != 0 }

    // not equals
    require constraint { maxMass != 0 }

    // less than
    require constraint { car.mass < maxMass }

    // less than or equal
    assume constraint { car.wheels <= maxWheels }

    // greater than
    require constraint { car.mass > minMass }

    // greater than or equal
    assume constraint { car.wheels >= minWheels }

    // conjunction
    require constraint { car.fuelTank.volume != 0 && car.engine.mass < car.mass }

    // disjunction
    require constraint { minMass < maxMass || minMass == maxMass }

    // complex condition (+ arithmetic operators)
    assume constraint {
      ((car.engine.mass < car.mass) && (car.engine.mass > 20))
        || (car.mass - car.engine.mass < 50)
    }
  }
}
