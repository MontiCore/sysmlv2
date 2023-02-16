package StateActions {

	attribute def VehicleStartSignal;
	attribute def VehicleOnSignal;
	attribute def VehicleOffSignal;

	part def Vehicle;

	action performSelfTest { in vehicle : Vehicle; }

	state def VehicleStates { in operatingVehicle : Vehicle; }

	state usage : VehicleStates;

	state vehicleStates {
		in operatingVehicle : Vehicle;

		entry; then off;

		state off;
		accept VehicleStartSignal
			then starting;

		state starting;
		accept VehicleOnSignal
			then on;

		state on;

		accept VehicleOffSignal
			then off;
	}
}
