/* (c) https://github.com/MontiCore/monticore */
package BumperBotPhysical {
  	/* Comment regarding the package BumperBotPhysical */
	private import ScalarValues::*; // not ex-exported
	import ISQ::TorqueValue as Torque; // Import Alias
	abstract part LegoBlock {
		/* A comment owned by
		     * the element Motor
		     */
		part usbPort : USBPort;
	}
	part USBPort;
	private part LegoUltraSonicSensor subsets LegoBlock {
	}
	alias LegoUltraSonicSensor as LUSS;
	part Motor subsets LegoBlock {
		value speed : Integer;
	}
	part NXTBrick subsets LegoBlock {
		part sensorPorts : USBPort[4] redefines usbPort; // this must break!
		part actuatorPorts : USBPort[4];
		part bb : BumperBotComponent; // physical NXT has SW component
	}
	part BumperBotComponent;
	interface def UsbCable {
		end USBPort;
		end USBPort;
	}
	part LegoRobot {
		part us : LUSS;
		part motors : Motor[2];
		part motor1: Motor;
		part motor2: Motor;
		part brick : NXTBrick;
		part battery : Battery;
		connect us::usbPort to brick::sensorPorts[1];
		connect brick::actuatorPorts[1] to motor1::usbPort[1];
		connect brick::actuatorPorts[2] to motor2::usbPort;
	}
	value type BatteryState {
		chargeState : Float;
		lastCharge : Integer;
	}
	value type Float;
	part Battery {
		value currentState : BatteryState; //state is keyword
	}

}
