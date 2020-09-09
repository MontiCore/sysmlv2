# Interfaces
In the following you can see the implementation of some overall connecting interfaces:

```
 AssociationEndMemberStd implements AssociationEndMember= DefinitionMemberPrefix
      (
        ["abstract"]? "end" (isComposite:"part")? PartProperty
        | ["abstract"]? "end" ["ref"]? ReferenceProperty
        | ["abstract"]?"end" ("ref" "action" | (isComposite:"action")? ) ActionUsage
      )
    ;
  BehaviorUsageMemberStd implements BehaviorUsageMember =
    DefinitionMemberPrefix
    (
      ( ["abstract"]?  ("ref" "action" | isComposite:"action") ActionUsage)
      | ( ["abstract"]?  ("ref" "state" | isComposite:"state") StateUsage)
      | ( ["abstract"]?  ("ref" "constraint" | isComposite:"constraint") ConstraintUsage)
      | ( ["abstract"]?  ("ref" "requirement" | isComposite:"requirement") RequirementUsage)
      | ( "perform" PerformActionUsage)
      | ( "exhibit" ExhibitStateUsage)
      | ( "assert" AssertConstraintUsage)
      | ( "satisfy" SatisfyRequirementUsage)
    )
  ;

  ReferencePropertyNonPortStructureUsageMember implements NonPortStructureUsageMember =
    ["abstract"]? "ref" direction:FeatureDirection? ReferenceProperty;

  NonPortStructureUsageMemberStd implements NonPortStructureUsageMember =
    //PartNonPortStructureUsageMember in IBD
    // ReferencePropertyNonPortStructureUsageMember on Top
    ( ["abstract"]? ["value"]? direction:FeatureDirection? ValueProperty )
    | ( ["abstract"]? ( "ref" "individual" | isComposite:"individual")
     ( direction:FeatureDirection )? IndividualUsage )
    | ( ["abstract"]? ( "ref" "timeslice" | isComposite:"timeslice")
          ( direction:FeatureDirection )? TimeSliceUsage )
    | ( ["abstract"]? ( "ref" "snapshot" | isComposite:"snapshot")
          ( direction:FeatureDirection )? SnapshotUsage )
    | ( ["abstract"]? "link" ConnectionUsage )
    | ( ["abstract"]? "connect" Connector )
    | ( ["abstract"]? "interface" InterfaceUsage )
    | ( ["abstract"]? "interface" InterfaceUsage )
    | ( "bind" BindingConnector )
    | ( ["abstract"]? "succession" Succession )
    | ( ["abstract"]? "stream" ItemFlow )
    | ( ["abstract"]? "flow" SuccessionItemFlow )
  ;
  //Some of the following PackagedUsageMember Implementations are already in its files.
  IndividualUsagePackagedUsageMember implements PackagedUsageMember =  ["abstract"]? "individual" IndividualUsage;
  TimeSliceUsagePackagedUsageMember implements PackagedUsageMember =   ["abstract"]? "timeslice" TimeSliceUsage;
  SnapshotUsagePackagedUsageMember implements PackagedUsageMember =    ["abstract"]? "snapshot" SnapshotUsage;
  ActionUsagePackagedUsageMember implements PackagedUsageMember =      ["abstract"]? "action" ActionUsage;
  StateUsagePackagedUsageMember implements PackagedUsageMember =       ["abstract"]? "state" StateUsage;
  ConstraintUsagePackagedUsageMember implements PackagedUsageMember =  ["abstract"]? "constraint" ConstraintUsage;
  RequirementUsagePackagedUsageMember implements PackagedUsageMember = ["abstract"]? "requirement" RequirementUsage;

```

The above definition is implemented as Interfaces and then distributed in the corresponding grammars.
```
 BehaviorUsageMemberActionUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix ["abstract"]?  ("ref" "action" | isComposite:"action") ActionUsage;
  BehaviorUsageMemberStateUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix ["abstract"]?  ("ref" "state" | isComposite:"state") StateUsage;
  BehaviorUsageMemberConstraintUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix ["abstract"]?  ("ref" "constraint" | isComposite:"constraint") ConstraintUsage;
  BehaviorUsageMemberRequirementUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix ["abstract"]?  ("ref" "requirement" | isComposite:"requirement") RequirementUsage;
  BehaviorUsageMemberPerformActionUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix "perform" PerformActionUsage;
  BehaviorUsageMemberExhibitStateUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix  "exhibit" ExhibitStateUsage;
  BehaviorUsageMemberAssertConstraintUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix "assert" AssertConstraintUsage;
  BehaviorUsageMemberSatisfyRequirementUsage implements BehaviorUsageMember =
    DefinitionMemberPrefix  "satisfy" SatisfyRequirementUsage;


  ReferencePropertyNonPortStructureUsageMember implements NonPortStructureUsageMember =
    ["abstract"]? "ref" direction:FeatureDirection? ReferenceProperty;
  NonPortStructureUsageMemberValueProperty implements NonPortStructureUsageMember =
    ["abstract"]? ["value"]? direction:FeatureDirection? ValueProperty;
  NonPortStructureUsageMemberIndividualUsage implements NonPortStructureUsageMember =
    ["abstract"]? ( "ref" "individual" | isComposite:"individual") direction:FeatureDirection? IndividualUsage;
  NonPortStructureUsageMemberTimeSliceUsage implements NonPortStructureUsageMember =
    ["abstract"]? ( "ref" "timeslice" | isComposite:"timeslice") direction:FeatureDirection? TimeSliceUsage;
  NonPortStructureUsageMemberSnapshotUsage implements NonPortStructureUsageMember =
    ["abstract"]? ( "ref" "snapshot" | isComposite:"snapshot") direction:FeatureDirection? SnapshotUsage;
  NonPortStructureUsageMemberConnectionUsage implements NonPortStructureUsageMember =
    ["abstract"]? "link" ConnectionUsage;
  NonPortStructureUsageMemberConnector implements NonPortStructureUsageMember =
    ["abstract"]? "connect" Connector;
  NonPortStructureUsageMemberInterfaceUsage implements NonPortStructureUsageMember =
    ["abstract"]? "interface" InterfaceUsage;
  NonPortStructureUsageMemberBindingConnector implements NonPortStructureUsageMember =
    "bind" BindingConnector;
  NonPortStructureUsageMemberSuccession implements NonPortStructureUsageMember =
    ["abstract"]? "succession" Succession;
  NonPortStructureUsageMemberItemFlow implements NonPortStructureUsageMember =
    ["abstract"]? "stream" ItemFlow;
  NonPortStructureUsageMemberSuccessionItemFlow implements NonPortStructureUsageMember =
    ["abstract"]? "flow" SuccessionItemFlow;
    
//Some of the following PackagedUsageMember Implementations are already in its files.
  IndividualUsagePackagedUsageMember implements PackagedUsageMember =  ["abstract"]? "individual" IndividualUsage;
  TimeSliceUsagePackagedUsageMember implements PackagedUsageMember =   ["abstract"]? "timeslice" TimeSliceUsage;
  SnapshotUsagePackagedUsageMember implements PackagedUsageMember =    ["abstract"]? "snapshot" SnapshotUsage;
  ActionUsagePackagedUsageMember implements PackagedUsageMember =      ["abstract"]? "action" ActionUsage;
  StateUsagePackagedUsageMember implements PackagedUsageMember =       ["abstract"]? "state" StateUsage;
  ConstraintUsagePackagedUsageMember implements PackagedUsageMember =  ["abstract"]? "constraint" ConstraintUsage;
  RequirementUsagePackagedUsageMember implements PackagedUsageMember = ["abstract"]? "requirement" RequirementUsage;

```