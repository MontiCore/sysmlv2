package 'UsageNameUpperCase' {
	block Vehicle {
		value CapitalizedMass : Real;
		value status : VehicleStatus;

		part CapitalizedStatus : Engine;

		ref driver : Person;
	}
}