private import ScalarValues::*;

library package ESA_SysML2_Solution_Metadata{
    doc /* This library package contains the metadata and #keyword customisations for the extended language specific to the ESA MBSE Methodology */

    private import ESA_SysML2_Solution::*;
    private import Metaobjects::SemanticMetadata;

    //Relations
    metadata def functionalFlow{
        :>annotatedElement:SysML::FlowConnectionUsage;
    }

    metadata def interact{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def influence{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def isValidIn{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def justify{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def respond{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def detail{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def occurIn{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def isActiveIn{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def involve{
        :>annotatedElement:SysML::Dependency;
    }

    metadata def realize{
        :>annotatedElement:SysML::Dependency;
    }

    //Elements
    metadata def l0Function :> SemanticMetadata{
        :>> baseType = l0Functions meta SysML::ActionUsage;
        :> annotatedElement:SysML::ActionUsage;
    }

    metadata def exchangeItem :> SemanticMetadata {
        :>> baseType = exchangeItems meta SysML::ItemUsage;
        :> annotatedElement:SysML::ItemUsage;
    }

    metadata def spaceSystem :> SemanticMetadata{
        :>> baseType = spaceSystems meta SysML::PartUsage;
        :> annotatedElement:SysML::PartUsage;
    }

    metadata def l0Product :> SemanticMetadata{
        :>> baseType = l0Products meta SysML::PartUsage;
        :> annotatedElement:SysML::PartUsage;
    }

    metadata def product :> SemanticMetadata{
        :>> baseType = products meta SysML::PartUsage;
        :> annotatedElement:SysML::PartUsage;
    }

    metadata def interfaceEnd :> SemanticMetadata{
        :>> baseType = interfaceEnds meta SysML::PortUsage;
        :> annotatedElement:SysML::PortUsage;
        :> annotatedElement:SysML::PortDefinition;
    }

    metadata def eSAInterface :>SemanticMetadata{
        :>> baseType = eSAInterfaces meta SysML::InterfaceUsage;
        :> annotatedElement:SysML::InterfaceUsage;
    }

    metadata def eSARequirement :> SemanticMetadata{
        :>> baseType = eSARequirements meta SysML::RequirementUsage;
        :> annotatedElement:SysML::RequirementUsage;
    }

    metadata def justification :> SemanticMetadata{
        :>> baseType = justifications meta SysML::RequirementUsage;
        :> annotatedElement:SysML::RequirementUsage;
    }

    metadata def userNeed :> SemanticMetadata{
        :>> baseType = userNeeds meta SysML::RequirementUsage;
        :> annotatedElement:SysML::RequirementUsage;
    }
    /* Custom package / annotation of packages is not supported in SysML2

    metadata def specification :> SemanticMetadata{
        :>> baseType = specifications meta SysML::Package;
        :> annotatedElement:SysML::Package;
    }
    */

    metadata def function :> SemanticMetadata{
        :>> baseType = functions meta SysML::ActionUsage;
        :> annotatedElement:SysML::ActionUsage;
    }

    metadata def externalSystemFunction :> SemanticMetadata{
        :>> baseType = externalSystemFunctions meta SysML::ActionUsage;
        :> annotatedElement:SysML::ActionUsage;
    }

    metadata def functionalChain :> SemanticMetadata{
        :>> baseType = functionalChains meta SysML::ItemUsage;
        :> annotatedElement:SysML::ItemUsage;
    }

    metadata def functionalScenarioAction :> SemanticMetadata{
        :>>baseType = functionalScenariosAction meta SysML::ActionUsage;
        :> annotatedElement:SysML::ActionUsage;
    }

    /* Not supported yet in this implementation

    metadata def missionScenarioOccurrence :> SemanticMetadata{
        :>>baseType = missionScenariosOccurrence meta SysML::OccurrenceUsage;
        :> annotatedElement:SysML::OccurrenceUsage;
    }

    metadata def functionalScenarioOccurrence :> SemanticMetadata{
        :>>baseType = functionalScenariosOccurrence meta SysML::OccurrenceUsage;
        :> annotatedElement:SysML::OccurrenceUsage;
    }

    metadata def physicalScenarioOccurrence :> SemanticMetadata{
        :>>baseType = physicalScenariosOccurrence meta SysML::OccurrenceUsage;
        :> annotatedElement:SysML::OccurrenceUsage;
    }
    */

    metadata def systemOfInterest :> SemanticMetadata{
        :>> baseType = systemsOfInterest meta SysML::PartUsage;
        :> annotatedElement:SysML::PartUsage;
    }

    metadata def missionLifecycle :> SemanticMetadata{
        :>> baseType = missionLifecycles meta SysML::StateUsage;
        :> annotatedElement:SysML::StateUsage;
    }

    metadata def missionPhase :> Metaobjects::SemanticMetadata {
	    :>> baseType = missionPhases meta SysML::StateUsage;
	    :> annotatedElement : SysML::StateUsage;
	}

    metadata def contextState :> Metaobjects::SemanticMetadata{
        :>> baseType = contextStates meta SysML::StateUsage;
        :> annotatedElement : SysML::StateUsage;
    }

    metadata def operationalMode :> Metaobjects::SemanticMetadata{
        :>> baseType = operationalModes meta SysML::StateUsage;
        :> annotatedElement : SysML::StateUsage;
    }

    metadata def operationalModesCycle :> Metaobjects::SemanticMetadata{
        :>> baseType = operationalModesCycles meta SysML::StateUsage;
        :> annotatedElement : SysML::StateUsage;
    }

     metadata def missionActivity :> SemanticMetadata{
        :>> baseType = missionActivities meta SysML::ActionUsage;
        :> annotatedElement : SysML::ActionUsage;
    }

    metadata def missionScenarioAction :> SemanticMetadata{
        :>> baseType = missionScenariosAction meta SysML::ActionUsage;
        :> annotatedElement : SysML::ActionUsage;
    }

    metadata def missionObjective :> SemanticMetadata{
        :>> baseType = missionObjectives meta SysML::UseCaseUsage;
        :> annotatedElement : SysML::UseCaseUsage;
    }

    metadata def capability :> SemanticMetadata{
        :>>baseType = capabilities meta SysML::UseCaseUsage;
        :> annotatedElement : SysML::UseCaseUsage;
    }

    metadata def externalSystem :> SemanticMetadata{
        :>> baseType = externalSystems meta SysML::PartUsage;
        :> annotatedElement : SysML::PartUsage;
    }

    metadata def eSAStakeholder :> SemanticMetadata{
        :>> baseType = eSAStakeholders meta SysML::PartUsage;
        :> annotatedElement:SysML::PartUsage;
    }

     metadata def verificationActivity :> SemanticMetadata{
        :>> baseType = verificationActivities meta SysML::ActionUsage;
        :> annotatedElement : SysML::ActionUsage;
    }

    metadata def verificationScenarioAction :> SemanticMetadata{
        :>> baseType = verificationScenariosAction meta SysML::VerificationCaseUsage;
        :> annotatedElement : SysML::VerificationCaseUsage;
    }
}
