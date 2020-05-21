// Need ability to expand and collapse containment structures. - Done
package sfriedenthal_VehicleModel_1{
    package Definitions{
        //these imports eliminate the need to import into each nested package
        import PartDefinitions::*;
        import PortDefinitions::*;
        import FlowDefinitions::*;
        import SignalDefinitions::*;
        import InterfaceDefinitions::*;
        import ActionDefinitions::*;
        import StateDefinitions::*;
        import ValueDefinitions::*;
        package PartDefinitions{
            block Vehicle {
                value mass :> ISQ::mass;
                value brakePedalDepressed: Boolean;
                // Why is Time not part of ISQ library?
                value maintenanceTime: Time::DateTime;
                value Tmax: ISQ::TemperatureValue;
                // ref action providePower: ProvidePower;
                ref action performSelfTest: PerformSelfTest;
                ref action applyParkingBrake: ApplyParkingBrake;
                ref action senseTemperature: SenseTemperature(out temp);
                ref state vehicleStates: VehicleStates;
            }
            block Engine;
            block Cylinder;
            block Transmission;
            block Driveshaft;
            block AxleAssembly;
            block Axle;
            block FrontAxle:>Axle{
                value steeringAngle:ScalarValues::Real;
            }
            block HalfAxle;
            block Differential;
            block Wheel;
            block VehicleController{
                ref state controllerStates: ControllerStates;
            }
        }
        package PortDefinitions{
            port def FuelCmdPort;
            port def DrivePwrPort;
            port def ClutchPort;
            port def ShaftPort_a;
            port def ShaftPort_b;
            port def ShaftPort_c;
            port def ShaftPort_d;
            port def DiffPort;
            port def AxlePort;
            port def AxleToWheelPort;
            port def WheelToAxlePort;
            port def WheelToRoadPort;
            port def VehicleToRoadPort;
        }
        package FlowDefinitions{
            // This defines Torque as an alias for ISQ::Torque.
            alias ISQ::TorqueValue as Torque;
            value type FuelCmd;
        }
        package SignalDefinitions{
            value type VehicleStartSignal;
            value type VehicleOnSignal;
            value type VehicleOffSignal;
            value type StartSignal;
            value type OffSignal;
            value type OverTemp;
            value type ReturnToNormal;
            value type 'at(vehicle::maintenanceTime)';
            value type 'when(temp>vehicle::Tmax)';
        }
        package InterfaceDefinitions{
            interface def EngineToTransmissionInterface{
                end p1:DrivePwrPort;
                end p2:ClutchPort;
            }
        }
        package ActionDefinitions{
            // Originally these activities were defined with "parameters" defined
            // as flows properties between braces {...}, rather than true parameters
            // defined between parens (...).
            activity ProvidePower (
                in fuelCmd:FuelCmd,
                out wheelToRoadTorque:Torque[2]
            );
            activity GenerateTorque (
                in fuelCmd:FuelCmd,
                out engineTorque:Torque
            );
            activity AmplifyTorque (
                in engineTorque:Torque,
                out transmissionTorque:Torque
            );
            activity TransferTorque (
                in transmissionTorque:Torque,
                out driveshaftTorque:Torque
            );
            activity DistributeTorque (
                in driveshaftTorque:Torque,
                out wheelToRoadTorque:Torque[2]
            );
            activity PerformSelfTest;
            activity ApplyParkingBrake;
            activity SenseTemperature(out temp: ISQ::TemperatureValue);
        }    
        package StateDefinitions {
            state def VehicleStates;
            state def ControllerStates;  
        }
        package ValueDefinitions{
            import ScalarValues::*;
            // should we import ISQ::*;
        }
    }
    package VehicleConfigurations{
        // Don't need to import Definitions, if you are not using names directy under Definitions. 
        import Definitions::*;
        // Since ModelLibrary was imported, import PartDefinitions::*; would be sufficient here.
        import Definitions::PartDefinitions::*;
        import Definitions::PortDefinitions::*;
        import Definitions::FlowDefinitions::*;
        import Definitions::SignalDefinitions::*;
        import Definitions::InterfaceDefinitions::*;
        import Definitions::ActionDefinitions::*;
        import Definitions::StateDefinitions::*;
        import Definitions::ValueDefinitions::*;
        
        // It would be useful to have the option to recursively import all names in a package.
        
        package VehicleConfiguration_a{
            package VehiclePartsTree{
                part vehicle_a:Vehicle{
                    value mass redefines Vehicle::mass=1750;
                    part frontAxleAssembly:AxleAssembly{
                        part frontAxle:Axle;
                        part frontWheels:Wheel[2];
                    }
                    part rearAxleAssembly:AxleAssembly{
                        part rearAxle:Axle;
                        part rearWheels:Wheel[2];
                    }
                }
            }
            package VehicleActionTree{
                
            }
        }
        package VehicleConfiguration_b{
            import VehicleConfiguration_a::*;
            import VehicleConfiguration_a::VehiclePartsTree::*;
            package VehiclePartsTree{
                // vehicle_b specializes (i.e., is a kind of) vehicle_a
                part vehicle_b :> vehicle_a{
                    // It should be possible to get this same redefinition using the notation
                    // "value redefines mass=2000;" or "value :>> mass=2000;".
                    value mass redefines vehicle_a::mass=2000;
                    /*adding ports to parts. will add ports to blocks later and redefine here*/
                    port fuelCmdPort:FuelCmdPort;
                    port vehicleToRoadPort:VehicleToRoadPort{
                        port wheelToRoadPort1:WheelToRoadPort;
                        port wheelToRoadPort2:WheelToRoadPort;
                    }
                    perform VehicleActionTree::providePower;
                    // not defined on block perform VehicleActionTree::'provide power' redefines 'provide power';
                    perform VehicleActionTree::performSelfTest redefines performSelfTest;
                    perform VehicleActionTree::applyParkingBrake redefines applyParkingBrake;
                    perform VehicleActionTree::senseTemperature redefines senseTemperature;
                    exhibit States::vehicleStates redefines vehicleStates {
                        ref vehicle = self;
                        ref controller = vehicleController;
                    }
                    part frontAxleAssembly redefines vehicle_a::frontAxleAssembly{
                        port shaftPort_d:ShaftPort_d;
                        part frontAxle:FrontAxle redefines vehicle_a::frontAxleAssembly::frontAxle;
                        // This subsetting pattern needs to be simpified and generalized.
                        // Also need to have a general array and slice notation differentiated
                        // from multiplicity notation.
                        part frontWheel1:>frontWheels=frontWheels[1];
                        part frontWheel2:>frontWheels=frontWheels[2];
                    }
                    part rearAxleAssembly redefines vehicle_a::rearAxleAssembly{
                        port shaftPort_d:ShaftPort_d;
                        perform VehicleActionTree::providePower::distributeTorque;
                        part differential:Differential{
                            port shaftPort_d:ShaftPort_d;
                            port leftDiffPort:DiffPort;
                            port rightDiffPort:DiffPort;
                        }
                        part rearAxle redefines vehicle_a::rearAxleAssembly::rearAxle{
                            part leftHalfAxle:HalfAxle{
                                port leftAxleToDiffPort:AxlePort;
                                port leftAxleToWheelPort:AxlePort;
                            }
                            part rightHalfAxle:HalfAxle{
                                port rightAxleToDiffPort:AxlePort;
                                port rightAxleToWheelPort:AxlePort;
                            }
                        }
                        part rearWheel1:>rearWheels=rearWheels[1]{
                            port wheelToAxlePort:WheelToAxlePort;
                            port wheelToRoadPort:WheelToRoadPort;
                        }
                        part rearWheel2:>rearWheels=rearWheels[2]{
                            port wheelToAxlePort:WheelToAxlePort;
                            port wheelToRoadPort:WheelToRoadPort;
                        }
                        bind shaftPort_d=differential::shaftPort_d;
                        connect differential::leftDiffPort to rearAxle::leftHalfAxle::leftAxleToDiffPort;
                        connect differential::rightDiffPort to rearAxle::rightHalfAxle::rightAxleToDiffPort;
                        connect rearAxle::leftHalfAxle::leftAxleToDiffPort to rearWheel1::wheelToAxlePort;
                        connect rearAxle::rightHalfAxle::rightAxleToDiffPort to rearWheel2::wheelToAxlePort;
                    }
                    part engine:Engine{
                        port fuelCmdPort:FuelCmdPort;
                        port drivePwrPort:DrivePwrPort{
                            out engineTorque:Torque;
                        }
                        part cylinders:Cylinder[4];
                        perform VehicleActionTree::providePower::generateTorque;
                    }
                    part transmission:Transmission{
                        //conjugate notation
                        port clutchPort:~DrivePwrPort;
                        port shaftPort_a:ShaftPort_a;
                        perform VehicleActionTree::providePower::amplifyTorque;
                    }
                    part driveshaft:Driveshaft{
                        port shaftPort_b:ShaftPort_b;
                        port shaftPort_c:ShaftPort_c;
                        perform VehicleActionTree::providePower::transferTorque;
                    }
                    part vehicleController: VehicleController {
                        exhibit States::controllerStates redefines controllerStates;
                    }
                    // Would like to be able to group connections into subpackages.
                    bind vehicle_b::fuelCmdPort=vehicle_b::engine::fuelCmdPort;
                    connect engine::drivePwrPort to transmission::clutchPort; 
                    /** the following interface replaces the above connection. Would like to reference the stream (obj flow)*/
                    interface engineToTransmissionInterface:EngineToTransmissionInterface
                        connect p1=>engine::drivePwrPort to p2=>transmission::clutchPort{
                            // Since an item flow is an action, this could be "ref action" or even
                            // "perform". But perhaps it would be better to semantically distinguish this
                            // as "conveys" or "transfers".
                            ref generateToAmplify :> VehicleActionTree::providePower::generateToAmplify;
                        }
                    connect transmission::shaftPort_a to driveshaft::shaftPort_b; 
                    connect driveshaft::shaftPort_c to rearAxleAssembly::shaftPort_d;
                    bind rearAxleAssembly::rearWheel1::wheelToRoadPort=vehicle_b::vehicleToRoadPort::wheelToRoadPort1;
                    bind rearAxleAssembly::rearWheel2::wheelToRoadPort=vehicle_b::vehicleToRoadPort::wheelToRoadPort2;
                }
            }
            package VehicleActionTree{
                action providePower:ProvidePower{
                    // No successions (control flows) between these actions, because the
                    // flows between them are continuous streams.
                    action generateTorque:GenerateTorque (
                        // This is a shorthand for the bind commented out below.
                        in = providePower::fuelCmd,
                        out engineTorque);
                    action amplifyTorque:AmplifyTorque (
                        in engineTorque,
                        out transmissionTorque
                    );
                    action transferTorque:TransferTorque (
                        // This is a shorthand for the stream commented out below.
                        in transmissionTorque stream from transferTorque::transmissionTorque
                    );
                    action distributeTorque:DistributeTorque;
                    // bind providePower::fuelCmd=generateTorque::fuelCmd;
                    // Can't currently use the shorthand for this stream because it is named.
                    stream generateToAmplify from generateTorque::engineTorque to amplifyTorque::engineTorque;
                    // stream amplifyTorque::transmissionTorque to transferTorque::transmissionTorque;
                }
                action performSelfTest: PerformSelfTest;
                action applyParkingBrake: ApplyParkingBrake;
                action senseTemperature: SenseTemperature;
            }
            package States{
                state vehicleStates: VehicleStates {
                    ref vehicle : Vehicle;
                    ref controller : VehicleController;

                    state operatingStates {
                        entry action initial;

                        state off;                    
                        state starting;                    
                        state on {
                            entry vehicle::performSelfTest;
                            // was vehicle::providePower;
                            do VehicleActionTree::providePower;
                            exit vehicle::applyParkingBrake;
                        }

                        transition initial then off;

                        transition 'starting-on'
                            first starting
                            accept VehicleOnSignal
                            then on;

                        transition 'off-starting'
                            first off
                            accept VehicleStartSignal
                            if vehicle::brakePedalDepressed
                            // it is confusing to show signal as value type with parameters
                            do send StartSignal() to controller
                            then starting;

                        transition 'on-off'
                            first on
                            accept VehicleOffSignal
                            then off;
                    }

                    state healthStates {
                        entry action initial;
                        do vehicle::senseTemperature (out temp);

                        state normal;
                        state maintenance;
                        state degraded;                    

                        transition initial then normal;

                        transition 'normal-maintenance'
                            first normal
                            accept 'at(vehicle::maintenanceTime)'
                            then maintenance;

                        transition 'normal-degraded'
                            first normal
                            accept 'when(temp>vehicle::Tmax)' 
                            do send OverTemp() to controller
                            then degraded;

                        transition 'maintenance-normal'
                            first maintenance
                            accept ReturnToNormal
                            then normal;

                        transition 'degraded-normal'
                            first degraded
                            accept ReturnToNormal
                            then normal;
                    }
            }
                state controllerStates: ControllerStates {
                    state operatingStates {
                        entry action initial; 
                        state off;
                        state on;
                        
                        transition initial then off;
                        transition 'off-on'
                            first off
                            //why not a usage of StartSignal?
                            accept StartSignal
                            then on;
                        transition 'on-off'
                            first on
                            accept OffSignal
                            then off;
                    }
                }  
            }
        }
    }
}