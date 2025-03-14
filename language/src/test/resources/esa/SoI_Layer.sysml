private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package SoI_Layer{
    doc /*The goal of this layer is to identify the System of Interest (SoI) or system under design and the main external systems in interaction with our system. Its capabilities are also identified.*/

    package SystemCapabilities{
    #capability use case 'Data Exchange'{
        actor ::>Transversal_Layer::'ESA/ESOC Ground Segment';
    }
    #capability use case 'Transmit Telecommand Data':>'Data Exchange'{

    }
    #capability use case 'Receive Telemetry Data':>'Data Exchange'{

    }
    #capability use case 'Science Data Acquisition':>'Data Exchange'{

    }
    #capability use case 'Maintain Solar Arrays pointing'{
        actor ::>Transversal_Layer::'ESA/ESOC Ground Segment';
        include 'Calculate optimal pointing angle';
    }

    #capability use case 'Calculate optimal pointing angle';

    #capability use case 'Perform audit on C02 emissions'{
        actor ::>Transversal_Layer::'Law and policy actor';
    }
    }

    package SystemContext{
        #interact dependency from Transversal_Layer::'Launcher Segment' to Transversal_Layer::'ESA/ESOC Ground Segment';
        #interact dependency from Transversal_Layer::'ESA/ESOC Ground Segment' to Transversal_Layer::'Launcher Segment';
        #interact dependency from MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment' to Transversal_Layer::'ESA/ESOC Ground Segment';
        #interact dependency from Transversal_Layer::'ESA/ESOC Ground Segment' to MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment';
        #interact dependency from MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment' to Transversal_Layer::'Launcher Segment';
        #interact dependency from MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment' to Transversal_Layer::'Coastal and Maritime Biologist';
        #interact dependency from MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment' to Transversal_Layer::'Environmental Scientist';
        #interact dependency from MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment' to Transversal_Layer::'Earth/Ocean';
        #interact dependency from Transversal_Layer::'Weather Forecaster' to MissionSpec_Layer::MissionContext::'Eagle Eye Mission'::'Space Segment';
        #influence dependency from Transversal_Layer::'Law and policy actor' to MissionSpec_Layer::MissionContext::'Eagle Eye Mission';
    }
}
