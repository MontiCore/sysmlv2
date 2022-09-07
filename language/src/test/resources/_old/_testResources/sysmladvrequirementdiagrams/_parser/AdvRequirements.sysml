package AdvRequirements {

  adv requirement def MassPowerLimitationRequirement(maxMass: double, maxPower: double) {
    subject parameters (
      in port1: double,
      out port2: Port,
      inout port3: Port,
      in stream port4: Port,
      out stream port5: Port,
      inout stream port6: Port,
      actualMass: double
    );

    // assumptions & constraints
    assume constraint { maxMass > 0 }
    require constraint { actualMass < maxMass }

    // requirement reference
    require advReqUsageRef;
  }

  // requirement usage
  adv requirement massPowerLimitationRequirement:
    AdvMassLimitationRequirement(maxMass = 100.0, maxPower = 100.0) {

    // requirement reference
    require advReqUsageRef;
  }

  part mass: Mass;
  part power: Power;

  // TODO: later maybe only use types, instead of usages (adhoc or otherwise)
  satisfy massPowerLimitationRequirement
    using (
      part mass: Mass;,
      //part power: Power,
      actualPower=power.actualPower
    );
}
