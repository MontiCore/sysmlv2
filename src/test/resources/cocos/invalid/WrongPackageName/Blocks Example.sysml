package 'WrongName' {
	import ScalarValues::*;
	
	block vehicle {
		value mass : Real;
		value status : VehicleStatus;
		
		part eng : Engine;
		
		ref driver : Person;
	}
	
	value type VehicleStatus {
		gearSetting : Integer;
		acceleratorPosition : Real;
	}
	
	block Engine;	
	block Person;
}