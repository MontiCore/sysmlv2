private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package Physical_Layer{
    doc /*The goal of this layer is to specify the system decomposition into lower-level products/components, which will implement the functions specified in the Functional design layer.*/

    #l0Product part 'Space Segment'{
        #product part 'Avionics Platform'{
            #product part 'Time Reference';
            #product part 'Sensors and Actuators';
            #product part 'Solar Array';
            #product part 'AOC System';
            #product part Thruster;
            #product part 'On Board Computer';
            #product part Magnetorquer;
            #product part 'Communication System';

            #interfaceEnd port AOCScommandPortIN:~Physical_Layer::AOCScommandPortOUTDef;
            #interfaceEnd port PayloadCommandingPortOUT:~Physical_Layer::PayloadCommandingPortINDef;
            #interfaceEnd port PayloadDirectMonitoringPortIN:Physical_Layer::PayloadDirectMonitoringPortINDef;
            #interfaceEnd port PayloadSynchronizationPortOUT:~Physical_Layer::PayloadSynchronizationPortINDef;
            #interfaceEnd port PayloadControlPortOUT:~Physical_Layer::PayloadControlPortINDef;
            #interfaceEnd port TMCADUsPortOUT:Physical_Layer::TMCADUsPortOUTDef;
            #interfaceEnd port TM_CLTUsPortIN:Physical_Layer::TMCLTUsPortINDef;
        }
        #product part Payload{
            #interfaceEnd port PayloadCommandingPortIN:Physical_Layer::PayloadCommandingPortINDef;
            #interfaceEnd port PayloadDirectMonitoringPortOUT:~Physical_Layer::PayloadDirectMonitoringPortINDef;
            #interfaceEnd port PayloadSynchronizationPortIN:Physical_Layer::PayloadSynchronizationPortINDef;
            #interfaceEnd port PayloadControlPortIN:Physical_Layer::PayloadControlPortINDef;
        }
            #eSAInterface interface IF1 connect
            inflow ::> Transversal_Layer::'ESA/ESOC Ground Segment'.AOCScommandPortOUT  to outflow ::> 'Space Segment'.'Avionics Platform'.AOCScommandPortIN ;
            #eSAInterface interface IF2 connect
            inflow ::> Transversal_Layer::'ESA/ESOC Ground Segment'.'TM_CLTUsPortOUT' to outflow ::> 'Space Segment'.'Avionics Platform'.'TM_CLTUsPortIN';
            #eSAInterface interface IF3 connect
            inflow ::> 'Space Segment'.'Avionics Platform'.'TMCADUsPortOUT' to outflow ::> Transversal_Layer::'ESA/ESOC Ground Segment'.'TMCADUsPortIN';

            #eSAInterface interface IF4 connect
            inflow ::> 'Space Segment'.'Avionics Platform'.'PayloadCommandingPortOUT' to outflow ::> 'Space Segment'.'Payload'.'PayloadCommandingPortIN';
            #eSAInterface interface IF5 connect
            inflow ::> 'Space Segment'.'Avionics Platform'.'PayloadControlPortOUT' to outflow ::> 'Space Segment'.'Payload'.'PayloadControlPortIN';
            #eSAInterface interface IF6 connect
            inflow ::> 'Space Segment'.'Avionics Platform'.'PayloadSynchronizationPortOUT' to outflow ::> 'Space Segment'.'Payload'.'PayloadSynchronizationPortIN';
            #eSAInterface interface IF7 connect
            inflow ::> 'Space Segment'.'Payload'.'PayloadDirectMonitoringPortOUT' to outflow ::> 'Space Segment'.'Avionics Platform'.'PayloadDirectMonitoringPortIN';
    }


    #interfaceEnd port def AOCScommandPortOUTDef{
        out ::>Transversal_Layer::'AOCS Commands';
    }

    #interfaceEnd port def PayloadCommandingPortINDef{
        in ::>Transversal_Layer::'Payload Commanding';
    }

    #interfaceEnd port def PayloadDirectMonitoringPortINDef{
        in ::>Transversal_Layer::'Payload Direct Monitoring';
    }

    #interfaceEnd port def PayloadSynchronizationPortINDef{
        in ::>Transversal_Layer::'Payload Synchronization';
    }

    #interfaceEnd port def PayloadControlPortINDef{
        in ::>Transversal_Layer::'Payload Control';
    }

    #interfaceEnd port def TMCADUsPortOUTDef{
        out ::>Transversal_Layer::'TM CADUs';
    }

    #interfaceEnd port def TMCLTUsPortINDef{
        in ::>Transversal_Layer::'TM CLTUs';
    }
}
