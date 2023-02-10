package StatesActions {

	part def Vehicle{



	exhibit state vehicleStates {
		entry; then off;

		state off;
		then starting;

		state starting;
		then on;

		state on;
		then off;
	}
	}

	part bmw : Vehicle;

}
