package StatesActions {

	attribute def VehicleStartSignal;
	attribute def VehicleOnSignal;
	attribute def VehicleOffSignal;

	part def Vehicle{


	state def VehicleStates;

	state vehicleStates : VehicleStates {
		entry; then off;

		state off;
		then starting;

		state starting;
		then on;

		state on;
		then off;
	}
	}

}
