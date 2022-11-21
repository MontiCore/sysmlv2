state def Initial;

state def Machine {
  entry;
  then I;

  state I;

  transition
    first I
    accept someEvent
    if condition
    do action someAction
    then O;

}

exhibit state Behavior {
  entry;
  then S;
}
		state operationalStates {
			entry;
			do action {send a to b;}
      exit actionXY;
      then off;
			}

	state vehicleStates : VehicleStates parallel {

		state operationalStates {
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

		state healthStates;
	}

