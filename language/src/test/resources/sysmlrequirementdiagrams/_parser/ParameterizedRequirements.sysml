package ParameterizedRequirements {
  requirement def id '1' MassLimitationRequirement (actualMass: Real, maxMass: Real) {
    subject vehicle: Vehicle;
  }

  requirement def id '2' PowerLimitationRequirement (actualPower: Real, maxPower: Real = 250) {
    subject vehicle: Vehicle;
  }

  requirement def id '3' NewPowerLimitationRequirement(actualPower = 200, minPower: Real = 100)
    :> PowerLimitationRequirement {
    subject vehicle: Vehicle;
  }

  requirement id '1.1' massLimitationRequirement:
    MassLimitationRequirement(actualMass = 100, maxMass = 200);

  requirement id '2.1' powerLimitationRequirement:
    PowerLimitationRequirement(actualPower = 200, minPower: Real = 100);

  part vehicle {
    part engine: Engine;
    satisfy massLimitationRequirement by vehicle;
    satisfy advMassLimitationRequirement by vehicle;
  }
}