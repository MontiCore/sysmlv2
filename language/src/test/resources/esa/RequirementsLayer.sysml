private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package RequirementsLayer{
    doc /*The goal of this layer is to capture the textual requirements in the model. These requirements will be linked to the other model elements and may either complement or formalize what is presented in the other model diagrams, depending on the approach selected by the project.*/

    package MySpecifications{
        package 'Mission Analysis Guidelines';
        package 'Launcher User Manual';
        package 'GenSRD';
        package 'Pointing Specification';
        package 'Environmental Specification';
        package 'Data Exchange Specification';

        dependency from 'Data Exchange Specification' to 'GenSRD';
        dependency from 'Data Exchange Specification' to 'Mission Analysis Guidelines';
        dependency from 'Launcher User Manual' to 'GenSRD';
        dependency from 'Mission Analysis Guidelines' to 'GenSRD';
    }

    package Requirements{
        #eSARequirement requirement <'REQ6'> 'Solar Array Drive Mechanism and Electronics 1'{
            doc /* Solar Array Drive Mechanism Electronics shall be able to rotate 1 turn per day and transfer up to 1000W */
            :>> status = RequirementStatusEnum::Proposed;
        }

        #eSARequirement requirement <'REQ7'> 'Solar Array Drive Mechanism and Electronics 2'{
            doc /*Some minor corrections shall be done, throughout the year, due to the sun-pointing angle evolution along 1 year*/
            :>> 'verification method' = VerificationMethodsEnum::Analysis;
        }

        #eSARequirement requirement <'REQ8'> 'Solar Array Deployment Mechanism 1'{
            doc /*A deployment arm is requested to position the Centre of Gravity in the SADM rotation axis, and by this, prevent perturbations at AOCS level*/
            :>> 'verification method' = VerificationMethodsEnum::Analysis;
        }

        #eSARequirement requirement <'REQ9'> 'Hold-Down and Release Mechanism 1'{
            doc /*The HDRM shall be used to maintain the Solar Array rigidly fixed to the spacecraft structure during launch*/
            :>> 'verification method' = VerificationMethodsEnum::Analysis;
        }

        #eSARequirement requirement <'REQ10'> 'Solar array panels deployment mechanism'{
            doc /*"The Solar Array panel deployment mechanisms shall: - First, release the lateral panels from the central panel; - Then deploy the lateral panels.*/
            :>> 'verification method' = VerificationMethodsEnum::Analysis;
        }

        #justification requirement <'JUS1'> 'Hold-Down and Release Mechanism 1 Justification'{
            doc /*In a launch environment (partially) deployed Solar Arrays could damage surrounding equipment and themselves due to excessive vibrations.*/
        }

        #userNeed requirement <'UN1'> 'Launch provider need'{
            doc /*Launch provider needs to be certain that payloads will not damage each other.*/
        }

        satisfy REQ6 by MissionSpec_Layer::MissionContext::'Eagle Eye Mission'.'Space Segment';
        satisfy REQ7 by MissionSpec_Layer::MissionContext::'Eagle Eye Mission'.'Space Segment';
        satisfy REQ8 by MissionSpec_Layer::MissionContext::'Eagle Eye Mission'.'Space Segment';
        satisfy REQ9 by MissionSpec_Layer::MissionContext::'Eagle Eye Mission'.'Space Segment';
        satisfy REQ10 by MissionSpec_Layer::MissionContext::'Eagle Eye Mission'.'Space Segment';

        #justify dependency from JUS1 to REQ9;

        #respond dependency from REQ9 to UN1;

        #derivation connection{
            end #original ::> REQ7;
            end #derive ::> REQ9;
        }

        #derivation connection{
            end #original ::> REQ8;
            end #derive ::> REQ10;
        }
    }
}
