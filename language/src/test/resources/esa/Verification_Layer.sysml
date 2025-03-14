private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package Verification_Layer{
    doc /*This layer aims to describe the verification plan and the verification scenarios for the requirements described in the requirement layer. Each verification scenario represents a test, analysis, review-of-design or inspection to be performed on a product (at a given level of decomposition) to verify that the requirements are met.*/

    #verificationScenarioAction verification 'Test Expected Max Power'{
        first start;
        then #verificationActivity action 'Request space segment to orient the solar panels';
        then #verificationActivity action 'Orient the solar panel wrt sun light simulator';
        then #verificationActivity action 'Measure the output electrical power';
        then #verificationActivity action 'Compare to the expected max power';
        then done;

        objective testExpectedMaxPower{
            verify RequirementsLayer::Requirements::REQ6;
        }
    }
}
