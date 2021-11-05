package RequirementDefinitions {
  requirement def id '1' Requirement1 :> MassLimitationRequirement {
    assume constraint { vehicle::fuelMass > 0 }
  }

  requirement def id '2' Requirement2 :> MassLimitationRequirement {
    assume constraint { vehicle::fuelMass > 0 }
  }
}