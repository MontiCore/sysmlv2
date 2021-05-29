/* (c) https://github.com/MontiCore/monticore */
package eseidewitz_VehicleModel_update{
    package ModelLibrary{
        package PartDefinitions{
            block Vehicle {
                value mass:ScalarValues::Real;
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
            value type Torque;
            value type FuelCmd;
        }
        package InterfaceDefinitions{
            import PortDefinitions::*;
            interface def EngineToTransmissionInterface{
                end p1:DrivePwrPort;
                end p2:ClutchPort;
            }
        }
        package ActionDefinitions{
            import FlowDefinitions::*;
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
        }
        package ValueDefinitions{
            import ScalarValues::*;
        }
    }
    package VehicleConfigurations{
        import ModelLibrary::*;
        import ModelLibrary::PartDefinitions::*;
        import ModelLibrary::PortDefinitions::*;
        import ModelLibrary::FlowDefinitions::*;
        import ModelLibrary::InterfaceDefinitions::*;
        import ModelLibrary::ActionDefinitions::*;
        import ModelLibrary::ValueDefinitions::*;
        package VehicleConfiguration_a{
            package VehiclePartsTree{
                part vehicle_a:Vehicle{
                    value mass redefines ISQ::mass=1750;
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
                part vehicle_b :> vehicle_a{
                    value mass redefines ISQ::mass=2000;
                    //*adding ports to parts. will add ports to blocks later and redefine as necessary*/
                    port fuelCmdPort:FuelCmdPort;
                    port vehicleToRoadPort:VehicleToRoadPort{
                        port wheelToRoadPort1:WheelToRoadPort;
                        port wheelToRoadPort2:WheelToRoadPort;
                    }
                    perform VehicleActionTree::providePower;
                    part frontAxleAssembly:>vehicle_a::frontAxleAssembly{
                        port shaftPort_d:ShaftPort_d;
                        part frontAxle:FrontAxle redefines vehicle_a::frontAxleAssembly::frontAxle;
                        part frontWheel1:>frontWheels=frontWheels[1];
                        part frontWheel2:>frontWheels=frontWheels[2];
                    }
                    part rearAxleAssembly:>vehicle_a::rearAxleAssembly{
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
                        port clutchPort:ClutchPort{
                            in engineTorque:Torque;
                        }
                        port shaftPort_a:ShaftPort_a;
                        perform VehicleActionTree::providePower::amplifyTorque;
                    }
                    part driveshaft:Driveshaft{
                        port shaftPort_b:ShaftPort_b;
                        port shaftPort_c:ShaftPort_c;
                        perform VehicleActionTree::providePower::transferTorque;
                    }
                    bind vehicle_b::fuelCmdPort=vehicle_b::engine::fuelCmdPort;
                    connect engine::drivePwrPort to transmission::clutchPort; 
                    /** the following interface replaces the above connection. Would like to reference the stream (obj flow)*/
                    interface engineToTransmissionInterface:EngineToTransmissionInterface
                        connect p1=>engine::drivePwrPort to p2=>transmission::clutchPort{
                            ref generateToAmplify:>VehicleActionTree::providePower::generateToAmplify;
                        }
                    connect transmission::shaftPort_a to driveshaft::shaftPort_b; 
                    connect driveshaft::shaftPort_c to rearAxleAssembly::shaftPort_d;
                    bind rearAxleAssembly::shaftPort_d=rearAxleAssembly::differential::shaftPort_d;
                    connect rearAxleAssembly::differential::leftDiffPort to rearAxleAssembly::rearAxle::leftHalfAxle::leftAxleToDiffPort;
                    connect rearAxleAssembly::differential::rightDiffPort to rearAxleAssembly::rearAxle::rightHalfAxle::rightAxleToDiffPort;
                    connect rearAxleAssembly::rearAxle::leftHalfAxle::leftAxleToDiffPort to rearAxleAssembly::rearWheel1::wheelToAxlePort;
                    connect rearAxleAssembly::rearAxle::rightHalfAxle::rightAxleToDiffPort to rearAxleAssembly::rearWheel2::wheelToAxlePort;
                    bind rearAxleAssembly::rearWheel1::wheelToRoadPort=vehicle_b::vehicleToRoadPort::wheelToRoadPort1;
                    bind rearAxleAssembly::rearWheel2::wheelToRoadPort=vehicle_b::vehicleToRoadPort::wheelToRoadPort2;
                    }
            }
            package VehicleActionTree{
                action providePower:ProvidePower(in fuelCmd, out wheelToRoadTorque) {
                    action generateTorque:GenerateTorque(in fuelCmd, out engineTorque);
                    then action amplifyTorque:AmplifyTorque(in engineTorque, out transmissionTorque);
                    then action transferTorque:TransferTorque(in transmissionTorque);
                    then action distributeTorque:DistributeTorque;
                    bind providePower::fuelCmd=generateTorque::fuelCmd;
                    stream generateToAmplify from generateTorque::engineTorque to amplifyTorque::engineTorque;
                    stream amplifyTorque::transmissionTorque to transferTorque::transmissionTorque;
                }
            }
        }
    }
}
