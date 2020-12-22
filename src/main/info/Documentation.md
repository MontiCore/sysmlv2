# Important Interfaces
- Unit: Interface which defines the first production of the grammar.
- PackagedDefinitionMember: Definitions in a package
- PackagedUsageMember: Usages of definitions in a package

- Defining a DefinitionBody
    - NestedDefinitionMember
    - NestedUsageMember
-  Defining Definition Memberships
    - NonPortStructureUsageMember
    - BehaviorUsageMember

- Definining Association Usages
    - AssociationEndMember
    - EndPortMember
    - ConjugatedEndPortMember
    
# Symbols
In general there SysML uses the name __SysMLName__, which may be a MCName or a name including a whitespace in single
 qoutation marks. 

If an element __references__ another element it does this with a __QualifiedName__ (Which also uses SysMLName).
Usually if an element uses the name __SysMLName__, it __defines__ a new name and is a 
symbol.

SysMLName is sometimes used directly in a NT. But often the often NTs are used for definition:
- ```ClassifierDeclarationCompletion ```(Currently implemented by 
```ClassifierDeclarationCompletionStd```)
- ```SysMLNameAndTypePart```

SysMLName is not a ```String``` and MontiCore expects the following:
- An symbol has the method ```getName()```
- ```getName()``` always returns a ```String```

This is why we use ```astrule``` to fulfill these requirements:
``` 
astrule <MyNTNameHere> =
    method public String getName(){
      return this.getSysMLName().getName();
    }
  ;
```

# Interface Implementations
In the following you can see the implementation of some overall connecting interfaces:
The definitions are bundled here, but they are bundled and rewritten in the corresponding grammar.

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