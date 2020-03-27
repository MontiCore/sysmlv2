/* (c) https://github.com/MontiCore/monticore */
package Blocks Example {
	import ScalarValues::*;
	
	block Vehicle {
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
