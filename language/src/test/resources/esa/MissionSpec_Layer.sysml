private import ESA_SysML2_Solution::*;
private import ESA_SysML2_Solution_Metadata::*;
private import ScalarValues::*;
private import RequirementDerivation::*;

package MissionSpec_Layer{
    doc /*The space system is the system that performs the mission. It is usually composed at least of a space segment, a ground segment and a launch segment.
    Usually, the system under our responsibility is not the complete space system, but a subcomponent of it, such as the payload or the ground segment. This system is called system under design or system of interest (SoI). The notion of system of interest is relative: if your system of interest is the payload, the service module will be an external system for you, while the opposite will be true for the team in charge of the service module.
    Even if the space system is not our SoI, we are still interested in having an overview of it. The goal of the Mission specification layer is to define the main elements of the mission and the space system, such as the mission phases, mission objectives or the mission scenarios.
    */

    package MissionObjectives{
        #missionObjective use case 'Manage and control exploitation of coastal and maritime areas'{
            actor ::>Transversal_Layer::'Law and policy actor';
        }
        #missionObjective use case 'Obtain data during a long lifetime'{
            actor ::>Transversal_Layer::'Law and policy actor';
            actor ::>Transversal_Layer::'Coastal and Maritime Biologist';
            actor ::>Transversal_Layer::'Environmental Scientist';
        }
        #missionObjective use case 'Obtain data systematically with high accuracy, reliability, and availability'{
            actor ::>Transversal_Layer::'Law and policy actor';
            actor ::>Transversal_Layer::'Coastal and Maritime Biologist';
            actor ::>Transversal_Layer::'Environmental Scientist';
            actor ::>Transversal_Layer::'Weather Forecaster';
        }
        #missionObjective use case 'Monitor coastal zones health and living conditions'{
            actor ::>Transversal_Layer::'Law and policy actor';
            actor ::>Transversal_Layer::'Coastal and Maritime Biologist';
            actor ::>Transversal_Layer::'Environmental Scientist';
        }
        #missionObjective use case 'Investigate climate change'{
            actor ::>Transversal_Layer::'Coastal and Maritime Biologist';
            actor ::>Transversal_Layer::'Environmental Scientist';
            actor ::>Transversal_Layer::'European Union';
        }
        #missionObjective use case 'Monitor ocean health and living conditions'{
            actor ::>Transversal_Layer::'Coastal and Maritime Biologist';
            actor ::>Transversal_Layer::'Environmental Scientist';
        }
        #missionObjective use case 'Forecast atmospheric and oceanic weather'{
            actor ::>Transversal_Layer::'Environmental Scientist';
        }
        #missionObjective use case 'Obtain scientific data in close-real time'{
            actor ::>Transversal_Layer::'Environmental Scientist';
        }
    }

    package MissionContext{
        #spaceSystem part 'Eagle Eye Mission'{
            #systemOfInterest part 'Space Segment'{
                perform 'Commission Platform';
                perform 'Commission Payload';
                perform 'Collect Orbital Data';
                perform 'Manoeuvre Spacecraft';
                perform 'Achieve Fine Pointing';
                perform 'Acquire Observation Data';
                perform 'Perform Disposal Manoeuvre';
                perform 'Passivate the Spacecraft';
            }
            ::>Transversal_Layer::'Launcher Segment';
            ::>Transversal_Layer::'ESA/ESOC Ground Segment';
            exhibit 'Mission Phases and Context States';
        }

        #interact dependency from 'Eagle Eye Mission' to Transversal_Layer::'Customer';
        #interact dependency from Transversal_Layer::'Customer' to 'Eagle Eye Mission';
        #interact dependency from 'Eagle Eye Mission' to Transversal_Layer::'Earth/Ocean';
        #interact dependency from Transversal_Layer::'ESA/ESOC Ground Segment' to 'Eagle Eye Mission';
        #interact dependency from 'Eagle Eye Mission' to Transversal_Layer::'ESA/ESOC Ground Segment';
        #influence dependency from Transversal_Layer::'European Union' to 'Eagle Eye Mission';
    }

    #missionLifecycle state 'Mission Phases and Context States'{
        entry;
        then #missionPhase state LaunchAndEarlyOrbitPhase{
            entry;
            then #missionPhase state Launch;
            then #missionPhase state 'Orbit Alignment';
         }

        then #missionPhase state CommissioningPhase;
        then #missionPhase state OperationsPhase{
            #contextState state 'Night Side';
            then #contextState state 'Day Side';
            then 'Night Side';
        }
        then #missionPhase state MaintenancePhase;
        then #missionPhase state DeOrbitingPhase;
        then done;
    }

    #missionActivity action 'Commission Platform';
    #missionActivity action 'Commission Payload';
    #missionActivity action 'Collect Orbital Data';
    #missionActivity action 'Perform Orbit Determination'{
            out 'Orbit compliance': String;
        }
    #missionActivity action 'Manoeuvre Spacecraft';
    #missionActivity action 'Achieve Fine Pointing';
    #missionActivity action 'Acquire Observation Data'{
            out 'End of life reached' : String;
        }
    #missionActivity action 'Send Disposal Command';
    #missionActivity action 'Perform Disposal Manoeuvre';
    #missionActivity action 'Passivate the Spacecraft';

    #missionScenarioAction action 'Mission Scenario Action 1' {
        first start;
        then 'Commission Platform';
        then 'Commission Payload';
        then merge 'merge 1';
        then 'Collect Orbital Data';
        then 'Perform Orbit Determination';
        then decide 'Is orbit compliant with target?';
            if 'Perform Orbit Determination'.'Orbit compliance' == "yes" then 'Achieve Fine Pointing';
            if 'Perform Orbit Determination'.'Orbit compliance' == "no" then 'Manoeuvre Spacecraft';

        then 'merge 1';

        then merge 'merge 2';
        then 'Acquire Observation Data';
        then decide 'Reached the End of Life?';
            if 'Acquire Observation Data'.'End of life reached' == "yes" then 'Send Disposal Command';
            if 'Acquire Observation Data'.'End of life reached' == "no" then 'merge 2';
        then 'Perform Disposal Manoeuvre';
        then 'Passivate the Spacecraft';
        then done;
    }
}
