state def VehicleStates {
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
