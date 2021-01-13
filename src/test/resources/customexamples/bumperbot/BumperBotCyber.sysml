package BumperBotCyber {
	import ScalarValues::*; // not ex-exported
	private part UltraSonic {
		port data : FOHP;
	}
	private port def FloatOutHistoryPort {
		out distance : Float;
		value history : History;
	}
	value type History;
	value type Float;
	alias FloatOutHistoryPort as FOHP;
	private part Motor {
		value speed : Integer;
		port cmd : IntegerInPort;
	}
	port def IntegerInPort {
		in input : IntegerInPort;
	}
	part Controller {
		port distance : ~FOHP; // port conjugation (invertation)
		port leftMotor : ~IntegerInPort;
		port rightMotor : ~IntegerInPort;
	}
	part MyBumperBot {
		part us 	: UltraSonic;
		part motors : Motor [2]; // an array of two Motors
		part ctrl 	: Controller;
		part leftMotor: Motor = motors[0];
		part rightMotor: Motor = motors[1];
		interface : UltraSonic2Controller connect
			_in => us::data to
			_out => ctrl::distance;
		interface : Controller2Motor connect
			_in => ctrl::leftMotor to
			_out => leftMotor::cmd;
		interface : Controller2Motor connect
			_in => ctrl::leftMotor to
			_out => rightMotor::cmd;
	}
	interface def UltraSonic2Controller {
		end _in : FOHP; //in and out are keywords
		end _out : ~FOHP;
	}
	interface def Controller2Motor {
		end _in : ~IntegerInPort;
		end _out : IntegerInPort;
	}
}