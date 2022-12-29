/* (c) https://github.com/MontiCore/monticore */
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
