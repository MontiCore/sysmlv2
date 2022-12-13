package StateActions {

  attribute def VehicleStartSignal;
  attribute def VehicleOnSignal;
  attribute def VehicleOffSignal;

  part def Vehicle;

  action performSelfTest {  }

  state def VehicleStates {  }

  state vehicleStates : VehicleStates {

    entry; then off;

    state off;
    accept VehicleStartSignal
      then starting;

    state starting;
    accept VehicleOnSignal
      then on;

    state on {
      entry performSelfTe;
      do action providePower { /* ... */ }
      exit action applyParkingBrake { /* ... */ }
    }
    accept VehicleOffSignal
      then off;
  }

}
