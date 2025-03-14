private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package Functional_Layer{
    doc /*The goal of this layer is to specify the internal behaviour of the System of Interest. This is done by performing a functional analysis of the system.*/

    #l0Function action 'Space Segment'{
        #function action 'Perform Central Processing'{
        //    #function action 'Attitude and Orbital Control'{
        //        #function action 'AOCS Equipment Management';
        //        #function action 'AOCS Phase Management';
        //    }
        //    #function action 'Planning / Autonomy';
        //    #function action 'Platform Management'{
        //        #function action 'RF Link Management';
        //        #function action 'Power Control';
        //        #function action 'Thermal Control';
        //        #function action 'Platform FDIR';
        //        #function action 'Deployment Sequence';
        //    }
        //    #function action 'System Management';
        //    #function action 'Payload Management'{
        //        #function action 'Processing for Payload Equipment Management';
        //        #function action 'Payload TC/TM Management';
        //        #function action 'Payload Modes Management';
        //        #function action 'Manage Recovery Procedures for Payload FDIR';
        //        #function action 'Execute Payload Monitoring Algorithms';
        //    }
        }
        #function action 'Test Platform Equipment';
        #function action 'Store Safe-Guard Memory Data';
        #function action 'Provide Time Reference';
        #function action 'Provide Platform Sensor and Actuator I/F';
        #function action 'Provide Mission Data Links';
        #function action 'Provide Command & Control Links';
        #function action 'Process Telecommands';
        #function action 'Process Platform Telemetry';
        #function action 'Process Essential TM';
        #function action 'Process Essential TC';
        #function action 'Perform Sensing and Actuation';
        #function action 'Perform Platform Data Storage';
        #function action 'Perform PIO';
        #function action 'Payload Processing'{
        //    #function action 'Perform Payload Data Storage';
        //    #function action 'Payload Data Routing';
        //    #function action 'Manage Payload Security';
        //    #function action 'Manage Data Acquisition Instruments';
        //    #function action 'Generate Payload Telemetry';
        }
        #function action 'Manage Reconfiguration';
        #function action 'Manage Platform Security';
        #function action 'Manage On-Board Time';
        #function action 'Initialisation';
        #function action 'Heat Up Thrusters';
        #function action 'Get Orbit Parameters';
        #function action 'Detumbling';
        #function action 'Deploy Solar Array';
        #function action 'Concentrate Sensor and Actuator Data';
        #function action 'Commisioning';
        #function action 'Activate Thrusters';
        #function action 'Achieve Coarse Pointing';
        ref #externalSystemFunction action 'Solar Array Dynamics Validation';

        #functionalFlow flow flow1 of Transversal_Layer::'Command & Control' from 'Solar Array Dynamics Validation' to 'Deploy Solar Array';
        #functionalFlow flow flow2 of Transversal_Layer::'Telemetry Data' from 'Deploy Solar Array' to 'Process Platform Telemetry';
        #functionalFlow flow flow3 of Transversal_Layer::'Telemetry Data' from 'Process Platform Telemetry' to 'Test Platform Equipment';
        #functionalFlow flow flow4 of Transversal_Layer::'Telecommand Data' from 'Test Platform Equipment' to 'Achieve Coarse Pointing';
        #functionalFlow flow flow5 of Transversal_Layer::'Command & Control feedback' from 'Achieve Coarse Pointing' to 'Test Platform Equipment';

        exhibit 'Space Segment Operational Modes';
    }

    #functionalChain item 'Pointing Chain'{
        ::> 'Space Segment'.'Activate Thrusters';
        ::> 'Space Segment'.'Perform Central Processing';
    }

    #functionalScenarioAction action FunctionalScenarioAction1{
        first start;
        then 'Space Segment'.'Solar Array Dynamics Validation';
        then 'Space Segment'.'Deploy Solar Array';
        then 'Space Segment'.'Process Platform Telemetry';
        then 'Space Segment'.'Test Platform Equipment';
        then 'Space Segment'.'Achieve Coarse Pointing';
        then 'Space Segment'.'Test Platform Equipment';
        then done;
    }

    /* cannot find the way to do more than 2 messages

    #functionalScenarioOccurrence occurrence FunctionalScenarioOccurrence1{
        first 'Space Segment'.flow1
        then 'Space Segment'.flow2;
        //then 'Space Segment'.flow3;
    }
    */

    #operationalModesCycle state 'Space Segment Operational Modes'{
        entry;
        then #operationalMode state 'Safe mode';
        then 'Eclipse mode';
        then 'Orbit maintenance mode';
        then 'Nominal mode';
        #operationalMode state 'Eclipse mode';
        then 'Safe mode';
        then 'Nominal mode';
        #operationalMode state 'Nominal mode';
        then 'Eclipse mode';
        then 'Safe mode';
        #operationalMode state 'Orbit maintenance mode';
        then 'Safe mode';
        then 'Eclipse mode';
        then 'Nominal mode';
    }

    #isValidIn dependency from 'Space Segment Operational Modes'::'Nominal mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::LaunchAndEarlyOrbitPhase;
    #isValidIn dependency from 'Space Segment Operational Modes'::'Nominal mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::CommissioningPhase;
    #isValidIn dependency from 'Space Segment Operational Modes'::'Nominal mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::OperationsPhase;

    #isValidIn dependency from 'Space Segment Operational Modes'::'Safe mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::DeOrbitingPhase;
    #isValidIn dependency from 'Space Segment Operational Modes'::'Safe mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::MaintenancePhase;

    #isValidIn dependency from 'Space Segment Operational Modes'::'Orbit maintenance mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::OperationsPhase;
    #isValidIn dependency from 'Space Segment Operational Modes'::'Orbit maintenance mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::MaintenancePhase;

    #isValidIn dependency from 'Space Segment Operational Modes'::'Eclipse mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::CommissioningPhase;
    #isValidIn dependency from 'Space Segment Operational Modes'::'Eclipse mode' to 'MissionSpec_Layer'::'Mission Phases and Context States'::OperationsPhase;
}
