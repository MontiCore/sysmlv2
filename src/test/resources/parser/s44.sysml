action def ChargeBattery {
  first start;

  then merge continueCharging;
  then action monitor : MonitorBattery {
    out item batteryCharge : Real;
  }

  then decide;
    if monitor::batteryCharge < 100 then addCharge;
    if monitor::batteryCharge >= 100 then endCharging;

  action addCharge : AddCharge {
    in item charge = monitor::batteryCharge;
  }
  then continueCharging;

  action endCharging : EndCharging;

  then done;
}
