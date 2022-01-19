state def VehicleStates {
  entry; then off;

  state off;

  transition off_to_starting
    first off
    accept VehicleStartSignal
    then starting;

  state starting;

  transition starting_to_on
    first starting
    accept VehicleOnSignal
    then on;

  state on;

  transition on_to_off
    first on
    accept VehicleOffSignal
    then off;
}
