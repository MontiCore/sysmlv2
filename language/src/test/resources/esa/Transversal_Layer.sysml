private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package Transversal_Layer{
    doc /*The goal of this layer is to identify the concepts that will be used throughout all the layers: the External Systems and the Exchange items.
    External System refers to any system, including human beings, that is in interaction with the Space system or System of Interest.
    Exchange item refers to any information, matter, energy, etc. that is exchanged between any two entities of our model (functions, products, etc.).
    Stakeholder is any entity (individual or organisation) with a legitimate interest in the system.*/

    #externalSystem part 'Environmental Scientist';
    #eSAStakeholder part 'European Union';
    #externalSystem part 'Weather Forecaster';
    #eSAStakeholder part 'Law and policy actor';
    #externalSystem part 'Coastal and Maritime Biologist';
    #externalSystem part Customer;
    #externalSystem part 'Earth/Ocean';
    #externalSystem part 'ESA/ESOC Ground Segment'{
        #interfaceEnd port AOCScommandPortOUT:Physical_Layer::AOCScommandPortOUTDef;
        #interfaceEnd port TMCADUsPortIN:~Physical_Layer::TMCADUsPortOUTDef;
        #interfaceEnd port TM_CLTUsPortOUT:~Physical_Layer::TMCLTUsPortINDef;
        perform Functional_Layer::'Space Segment'.'Solar Array Dynamics Validation';
    }
    #externalSystem part 'Launcher Segment';

    #exchangeItem item 'Command & Control';
    #exchangeItem item 'Telemetry Data';
    #exchangeItem item 'Telecommand Data';
    #exchangeItem item 'Command & Control feedback';
    #exchangeItem item 'Thruster Heated Up';
    #exchangeItem item 'New Orbit Data';
    #exchangeItem item 'Orbit Parameters';
    #exchangeItem item 'AOCS Control feedback';

    #exchangeItem item 'AOCS Commands';
    #exchangeItem item 'Payload Commanding';
    #exchangeItem item 'Payload Direct Monitoring';
    #exchangeItem item 'Payload Synchronization';
    #exchangeItem item 'Payload Control';
    #exchangeItem item 'TM CADUs';
    #exchangeItem item 'TM CLTUs';
}
