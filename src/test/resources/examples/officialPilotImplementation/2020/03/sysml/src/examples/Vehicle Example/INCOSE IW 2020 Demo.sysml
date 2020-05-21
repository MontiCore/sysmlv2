package 'INCOSE IW 2020 Demo' {
	value type FuelCmd;
	
	activity 'Provide Power'(
	    in fuelCmd : FuelCmd, 
	    out leftWheelTorque : ISQ::TorqueValue,
	    out rightWheelTorque : ISQ::TorqueValue);
	
	activity 'Perform Self Test';
	activity 'Apply Parking Brake';
	
	state def 'Vehicle States';
	
	block Vehicle {
	    port fuelCmdPort;
	    port wheelToRoadPort[2];
	    
	    value 'brake pedal depressed' : ScalarValues::Boolean;
	
	    perform action 'perform self test' : 'Perform Self Test';
	    perform action 'provide power' : 'Provide Power';
	    perform action 'apply parking brake' : 'Apply Parking Brake';
	    
	    exhibit state 'vehicle states' : 'Vehicle States';    
	}
	
	block 'Vehicle Controller';
	block Engine;
	block Transmission;
	block 'Axle Assembly';
	block Wheel;
	
	interface def Driveshaft {
	    port driveShaftIn;
	    port driveShaftOut;
	}
	
	action 'provide power function' : 'Provide Power' (
	    in fuelCmd : FuelCmd, 
	    out leftWheelTorque : ISQ::TorqueValue,
	    out rightWheelTorque : ISQ::TorqueValue) {
	    
	    bind generateTorque::fuelCmd = fuelCmd;
	    
	    action generateTorque (
	        in fuelCmd : FuelCmd,
	        out engineTorque : ISQ::TorqueValue);
	    
	    stream generateToAmplify 
	        from generateTorque::engineTorque 
	        to amplifyTorque::engineTorque;
	    
	    action amplifyTorque (
	        in engineTorque : ISQ::TorqueValue,
	        out transmissionTorque : ISQ::TorqueValue
	    );
	    
	    stream 
	        from amplifyTorque::transmissionTorque 
	        to distributeTorque::driveTorque;
	    
	    action distributeTorque (
	        in driveTorque : ISQ::TorqueValue,
	        out leftWheelTorque : ISQ::TorqueValue,
	        out rightWheelTorque : ISQ::TorqueValue
	    );
	    
	    bind leftWheelTorque = distributeTorque::leftWheelTorque;
	    bind rightWheelTorque = distributeTorque::rightWheelTorque;
	}
	
	action 'perform self test function': 'Perform Self Test' {
	    // ...
	}
	action 'apply parking brake function': 'Apply Parking Brake' {
	    // ...
	}
	
	value type OnSignal;
	value type OffSignal;
	value type StartSignal;
	
	state 'vehicle state machine': 'Vehicle States' {
	    entry; then off;
	
	    ref vehicle : Vehicle;
	    ref control : 'Vehicle Controller';
	    
	    state off;
	    
	    transition
	        first off
	        accept OnSignal
	        if vehicle::'brake pedal depressed'
	        do send StartSignal() to control
	        then on;
	
	    state on {
	        entry vehicle::'perform self test';
	        do vehicle::'provide power';
	        exit vehicle::'apply parking brake';
	    }
	    
	    transition
	        first on
	        accept OffSignal 
	        then off;
	}
	
	part 'vehicle behavior' : Vehicle {
	    perform 'provide power function' redefines 'provide power';
	    perform 'perform self test function' redefines 'perform self test';
	    perform 'apply parking brake function' redefines 'apply parking brake';
	
	    exhibit 'vehicle state machine' redefines 'vehicle states' {
	        ref redefines vehicle = self;
	        ref redefines control = vehicleController;
	    }
	
	    part vehicleController : 'Vehicle Controller';    
	}
	
	part 'vehicle configuration' : Vehicle {
	    port fuelCmdPort redefines Vehicle::fuelCmdPort;
	    
	    connect fuelCmdPort to engine::fuelCmdPort;
	    
	    part engine : Engine {
	        port fuelCmdPort;
	        port drivePwrPort;
	    }
	    
	    connect engine::drivePwrPort to transmission::clutchPort;
	    
	    part transmission : Transmission {
	        port clutchPort;
	        port shaftOutPort;
	    }
	    
	    interface driveshaft : Driveshaft
	        connect driveShaftIn => transmission::shaftOutPort 
	             to driveShaftOut => rearAxleAssembly::differential::diffShaftPort;
	    
	    part rearAxleAssembly : 'Axle Assembly' {
	        part differential {
	            port diffShaftPort;
	            port leftWheelPort;
	            port rightWheelPort;
	        }
	        
	        connect differential::leftWheelPort to leftWheel::wheelAxlePort;
	            
	        part leftWheel {
	            port wheelAxlePort;
	            port wheelToRoadPort;
	        }
	        
	        connect differential::rightWheelPort to rightWheel::wheelAxlePort;
	       
	        part rightWheel {
	            port wheelAxlePort;
	            port wheelToRoadPort;
	        }
	    }
	    
	    connect rearAxleAssembly::leftWheel::wheelToRoadPort to wheelToRoadPort;
	    connect rearAxleAssembly::rightWheel::wheelToRoadPort to wheelToRoadPort;
	    
	    port wheelToRoadPort redefines Vehicle::wheelToRoadPort;
	}
	
	part eseidewitz_vehicle :> 'vehicle behavior', 'vehicle configuration' {
	    port fuelCmdPort redefines 'vehicle configuration'::fuelCmdPort {
	        in fuelCmd : FuelCmd;
	    }
	
	    perform 'provide power function' (in fuelCmd = fuelCmdPort::fuelCmd) 
	        redefines 'vehicle behavior'::'provide power';
	
	    part redefines engine {
	        port fuelCmdPort redefines engine::fuelCmdPort {
	            in fuelCmd : FuelCmd;
	        }
	        
	        perform 'provide power function'::generateTorque (
	            in fuelCmd = fuelCmdPort::fuelCmd,
	            out engineTorque = drivePwrPort::engineTorque
	        );
	        
	        port drivePwrPort redefines engine::drivePwrPort {
	            out engineTorque : ISQ::TorqueValue;
	        }
	    }
	    
	    part redefines transmission {
	        port clutchPort redefines transmission::clutchPort {
	            in engineTorque : ISQ::TorqueValue;
	        }
	        
	        perform 'provide power function'::amplifyTorque (
	            in engineTorque = clutchPort::engineTorque,
	            out transmissionTorque = shaftOutPort::driveShaftTorque
	        );
	        
	        port shaftOutPort redefines transmission::shaftOutPort {
	            out driveShaftTorque : ISQ::TorqueValue;
	        }
	    }
	        
	    part redefines rearAxleAssembly {
	        part redefines differential {
	            port diffShaftPort redefines differential::diffShaftPort {
	                in diffShaftTorque : ISQ::TorqueValue;
	            }
	        
	            perform 'provide power function'::distributeTorque (
	                in driveTorque = diffShaftPort::diffShaftTorque,
	                out leftWheelTorque = leftWheelPort::diffWheelTorque,
	                out rightWheelTorque = rightWheelPort::diffWheelTorque
	            );
	        
	            port leftWheelPort redefines differential::leftWheelPort {
	                out diffWheelTorque : ISQ::TorqueValue;
	            }
	            port rightWheelPort redefines differential::rightWheelPort {
	                out diffWheelTorque : ISQ::TorqueValue;
	            }
	        }
	     }     
	}
}

