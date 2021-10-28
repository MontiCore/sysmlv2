package ParameterizedRequirements {
  requirement def id '1' MassLimitationRequirement (actualMass: Real, maxMass: Real);
  requirement def id '2' PowerLimitationRequirement (actualPower: Real, maxPower: Real = 250);
  requirement def id '3' NewPowerLimitationRequirement(actualPower = 200, minPower: Real = 100) :> PowerLimitationRequirement;

  requirement id '1.1' massLimitationRequirement:
    MassLimitationRequirement(actualMass = 100, maxMass = 200);

  requirement id '2.1' powerLimitationRequirement:
    PowerLimitationRequirement(actualPower = 200, minPower: Real = 100);
}