package DecisionExample {
	import ScalarValues::*;

	attribute def BatteryCharged;


	action def MonitorBattery { out charge : Real; }
	action def AddCharge { in charge : Real; }
	action def EndCharging;
	part beispiel{
	action ChargeBattery {
		first start;

		then continueCharging;
		merge continueCharging;

		then monitor;
		action monitor : MonitorBattery {
			out batteryCharge : Real;
						first start;
      			then done;
		}

		then decideNode;
		 decide decideNode;
			if monitor.batteryCharge < 100 then addCharge;
			if monitor.batteryCharge >= 100 then endCharging;

		action addCharge : AddCharge {
			in charge :Real = monitor.batteryCharge;
			first start;
			then done;
		}
		then continueCharging;

		action endCharging : EndCharging{
					first start;
    			then done;
		}
		then done;
	}
	}
}
