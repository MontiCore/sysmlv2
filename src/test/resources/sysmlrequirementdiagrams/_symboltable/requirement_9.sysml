package Requirement_9 {
  part def Vehicle;
  part def Engine;

  requirement requirementGroup1 {
    requirement nestedRequirement {
      subject vehicle: Vehicle;
    }
  }

  requirement requirementGroup2 {
    subject vehicle: Vehicle;
    requirement nestedRequirement {
      subject engine: Engine;
    }
  }

  requirement def RequirementDef1 {
    subject vehicle: Vehicle;
  }
  requirement def RequirementDef2 {
    subject engine: Engine;
  }
  requirement def MultipleInheritedRequirements :> RequirementDef1, RequirementDef2;
  requirement def SpecializedRequirement :> RequirementDef1 {
    subject engine: Engine;
  }

  requirement def MultipleSubjects {
    subject vehicle: Vehicle;
    subject engine: Engine;
  }
}
