private import ScalarValues::*;

library package ESA_SysML2_Solution {
    doc /* This library package contains the element definitions and element usages for the basetype elements onto which the metadata basetypes are fixed */

    private import ESA_SysML2_Solution_Metadata::*;

        part def SpaceSystem{
            doc /*System that contains at least a space, a ground or a launch segment
            NOTE Generally a space system is composed of all three segments and is supported by a support segment.
            Source: ECSS-S-ST-00-01C*/
        }
        part spaceSystems : SpaceSystem;

        state def MissionLifecycle{
            doc /*All phases in the life of a product from needs identification through disposal.
            Source: ECSS-S-ST-00-01C (life cycle)*/
        }
        state missionLifecycles : MissionLifecycle;

        state def MissionPhase{
            doc /*Each of the phases in the mission lifecycle*/

            attribute 'expected duration':String;
        }
        state missionPhases : MissionPhase;

        action def MissionActivity{
            doc /*Behaviour of a system as seen from outside*/
        }
        action missionActivities : MissionActivity;

        action def MissionScenarioAction :> MissionScenario;
        action missionScenariosAction : MissionScenarioAction :> missionScenarios;

        /* Not supported yet in this implementation

        occurrence def MissionScenarioOccurence :> MissionScenario;
        occurrence missionScenariosOccurence : MissionScenarioOccurence :> missionScenarios;

        occurrence def FunctionalScenarioOccurrence :> FunctionalScenario;
        occurrence functionalScenariosOccurrence : FunctionalScenarioOccurrence :> functionalScenarios;

        occurrence def PhysicalScenarioOccurrence :> PhysicalScenario;
        occurrence physicalScenariosOccurrence : PhysicalScenarioOccurrence :> physicalScenarios;
        */

        use case def MissionObjective{
            doc /*Description of the behavioral requirements of a mission and its interaction with external systems.
            Source: ISO/IEC/IEEE 2011 (adapted from use case)*/
        }
        use case missionObjectives : MissionObjective;

        part def SystemOfInterest{
            doc /*System whose life cycle is in consideration.
            Source: ISO/IEC/IEEE 2015*/
        }
        part systemsOfInterest : SystemOfInterest;

        use case def Capability{
            doc /*Description of the behavioral requirements of the SoI and its interaction with external systems.
            Source: ISO/IEC/IEEE 2011 (adapted from use case)*/
        }
        use case capabilities : Capability;

        state def ContextState{
            doc /*State in which the interactions with the external systems are relatively constant*/
        }
        state contextStates : ContextState;

        action def L0Function{
            doc /*Functional representation of the SoI*/
        }
        action l0Functions : L0Function;

        action def Function{
            doc /*	Intended effect of a product.
            Source: ECSS-S-ST-00-01C*/
        }
        action functions : Function;

        action def ExternalSystemFunction{
            doc /*Intended effect of an external system.
            Source: ECSS-S-ST-00-01C (adapted from function)*/
        }
        action externalSystemFunctions : ExternalSystemFunction;

        item def FunctionalChain{
            doc /*Logical grouping of functions*/
        }
        item functionalChains : FunctionalChain;

        abstract occurrence def MissionScenario{
            doc /*Dynamic of exchanges between the entities involved in the mission to achieve a service.
            Source: SEBoK v2.5 (adapted from scenario)*/
        }
        abstract occurrence missionScenarios : MissionScenario;

        abstract occurrence def FunctionalScenario{
            doc /*Dynamic of exchanges between the functions allowing the SoI to achieve a service.
            Source: SEBoK v2.5 (adapted from scenario)*/
        }
        abstract occurrence functionalScenarios : FunctionalScenario;

        abstract occurrence def PhysicalScenario{
            doc /*Dynamic of exchanges between the products allowing the SoI to achieve a service.
            Source: SEBoK v2.5 (adapted from scenario)*/
        }
        abstract occurrence physicalScenarios : PhysicalScenario;

        abstract occurrence def VerificationScenario{
            doc /*Series of steps to be executed together or sequentially verify one or more requirements*/
        }
        abstract occurrence verificationScenarios : VerificationScenario;

        action def FunctionalScenarioAction :> FunctionalScenario{
        }
        action functionalScenariosAction : FunctionalScenarioAction :> functionalScenarios;

        state def OperationalMode{
            doc /*Operational state of a spacecraft, subsystem or payload in which certain functions can be performed.
            Source: ECSS-E-ST-70-11C (mode)*/
        }
        state operationalModes : OperationalMode;

        state def OperationalModesCycle{
            doc /*Cycle representing the transitions between operational modes*/
        }
        state operationalModesCycles : OperationalModesCycle;

        part def L0Product{
            doc /*Physical representation of the SoI*/
        }
        part l0Products : L0Product;

        part def Product{
            doc /*Result of a process.
            Source: ECSS-S-ST-00-01C*/

            attribute 'category according to heritage':ProductCategoriesAccordingToHeritage;
        }
        part products : Product;

        port def InterfaceEnd{
            doc /*One side of an interface.
            Source: ECSS-E-ST-10-24C*/

            attribute 'responsible organization':String;
        }
        port interfaceEnds : InterfaceEnd;

        abstract interface def ESAInterface{
            doc /*Boundary where two or more products meet and interact.
            Source: ECSS-S-ST-00-01C*/

            attribute 'interface nature':InterfaceNatures;
        }
        abstract interface eSAInterfaces : ESAInterface;

        action def VerificationActivity{
            doc /*Step in a verification scenario to demonstrate the verification of a requirement*/
        }
        action verificationActivities : VerificationActivity;

        verification def VerificationScenarioAction :> VerificationScenario{
            attribute 'verification stage':StageTypes[1..*];
            attribute assumptions:String[*];
            attribute type:VerificationMethodsEnum;
            attribute result:VerificationResultStatus:=VerificationResultStatus::NONE;
        }
        verification verificationScenariosAction : VerificationScenarioAction :> verificationScenarios;

        enum def InterfaceNatures{
            enum 'Coordinate System and Time';
            enum 'Space Environment';
            enum 'Man-machine';
            enum Electrical;
            enum Optical;
            enum 'Thermal Control';
            enum Structural;
            enum Mechanisms;
            enum Propulsion;
            enum Aerothermodynamics;
            enum Hydraulics;
            enum 'Data representation';
            enum Software;
            enum Communications;
            enum Control;
            enum Operations;
            enum Materials;
        }
        enum def StageTypes{
            enum 'Post-landing';
            enum 'In-orbit';
            enum 'Pre-launch';
            enum 'Acceptance';
            enum 'Qualification';
        }

        enum def VerificationResultStatus{
            enum NONE;
            enum PASSED;
            enum FAILED;
        }

        enum def CloseOutStatusType{
            enum OPEN;
            enum CLOSED;
        }

        enum def ProductCategoriesAccordingToHeritage{
            enum A{
                doc /*Off-the-shelf product without modifications and
                • subjected to a qualification test programme at least as severe as that imposed by the actual project specifications including environment and
                • produced by the same manufacturer or supplier and using the same tools and manufacturing processes and procedures*/
            }
            enum B{
                doc /*Off-the-shelf product without modifications.
                However:
                It has been subjected to a qualification test programme less severe or different to that imposed by the actual project specifications (including environment).*/
            }
            enum C{
                doc /*Off-the-shelf product with modifications.
                Modification includes changes to design, parts, materials, tools, processes, procedures, supplier, or manufacturer.*/
            }
            enum D{
                doc /*Newly designed and developed product.*/
            }
        }

        part def ExternalSystem{
            doc /*	Role played by a user or any other system that interacts with the mission or SoI.
            Source: UML 2.5.1 specification (adapted from actor)*/
        }
        part externalSystems[*] : ExternalSystem;

        part def ESAStakeholder{
            doc /*Any entity (individual or organisation) with a legitimate interest in the system.
            Source: ECSS-E-ST-10-11C*/
        }
        part eSAStakeholders[*] : ESAStakeholder;

        item def ExchangeItem{
            doc /*Information/matter/energy exchanged between two entities*/
        }
        item exchangeItems[*] : ExchangeItem;

        enum def VerificationMethodsEnum{
            enum ROD;
            enum Inspection;
            enum Analysis;
            enum Test;
        }

        enum def RequirementTypesEnum{
            enum 'Functional requirement';
            enum 'Performance requirement';
            enum 'Mission requirement';
            enum 'Interface requirement';
            enum 'Environmental requirement';
            enum 'Operational requirement';
            enum 'Human factor requirement';
            enum '(Integrated) logistics support requirements';
            enum 'Physical requirement';
            enum 'Product Assurance (PA) induced requirement';
            enum 'Configuration requirement';
            enum 'Design requirement';
            enum 'Verification requirement';
        }

        enum def RequirementStatusEnum{
            enum Proposed;
            enum Reviewed;
            enum Endorsed;
            enum Rejected;
        }

        requirement def ESARequirement{
            doc /*Documented demand to be complied with.
            Source: ECSS-S-ST-00-01C (requirement)*/

            attribute 'verification method':VerificationMethodsEnum[1..*];
            attribute 'verification method justification':String;
            attribute 'verification success criteria':String;
            attribute 'requirement type':RequirementTypesEnum;
            attribute 'justification':String;
            attribute 'internal justification':String;
            attribute 'status':RequirementStatusEnum;
            attribute 'close-out status':CloseOutStatusType;
            attribute 'close-out rationale':String;
        }
        requirement eSARequirements : ESARequirement;

        requirement def Justification{
            doc /*Rationale for a requirement*/
        }
        requirement justifications : Justification;

        requirement def UserNeed{
            doc /*What is necessary for, or desired by, the user.
            Source: ECSS-E-ST-10-06C (need)*/
        }
        requirement userNeeds : UserNeed;

        /* Custom package / annotation of packages is not supported in SysML2

        package specifications{
            doc /*Document stating requirements.
            Source: ECSS-S-ST-00-01C // missing end of comment here is this part is to be uncommentted
            attribute reference : String;
        }
        */
}
