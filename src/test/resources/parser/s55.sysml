action performSelfTest(vehicle : Vehicle);

state def VehicleStates(operatingVehicle : Vehicle);

state vehicleStates : VehicleStates (
  operatingVehicle : Vehicle,
  controller : VehicleController) {

  entry; then off;

  state off;
  accept VehicleStartSignal
    if operatingVehicle::brakePedalDepressed
    do send ControllerStartSignal() to controller
    then starting;

  state starting;
  accept VehicleOnSignal
    then on;

  state on { }
  accept VehicleOffSignal
  then off;
}
