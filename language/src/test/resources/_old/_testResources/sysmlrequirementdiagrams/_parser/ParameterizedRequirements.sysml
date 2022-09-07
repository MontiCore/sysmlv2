package ParameterizedRequirements {
  requirement def <"1"> MassLimitationRequirement (actualMass: Real, maxMass: Real) {
    subject vehicle: Vehicle;
  }

  requirement def <"2"> PowerLimitationRequirement (actualPower: Real, maxPower: Real = 250) {
    subject vehicle: Vehicle;
  }

  requirement def <"3"> NewPowerLimitationRequirement:> PowerLimitationRequirement
  (actualPower = 200, minPower: Real = 100) {
    subject vehicle: Vehicle;
  }

  requirement <"1.1"> massLimitationRequirement:
    MassLimitationRequirement(actualMass = 100, maxMass = 200);

  requirement <"2.1"> powerLimitationRequirement:
    PowerLimitationRequirement(actualPower = 200, minPower: Real = 100);

  part vehicle {
    part engine: Engine;
    satisfy massLimitationRequirement by vehicle;
    satisfy advMassLimitationRequirement by vehicle;
  }
}
