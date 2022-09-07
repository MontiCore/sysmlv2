package RequirementDefinitions {
  requirement def <"1"> Requirement1 :> MassLimitationRequirement {
    assume constraint { vehicle::fuelMass > 0 }
  }

  requirement def <"2"> Requirement2 :> MassLimitationRequirement {
    assume constraint { vehicle::fuelMass > 0 }
  }
}
