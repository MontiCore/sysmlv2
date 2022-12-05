package ForkJoinExample {
	import ScalarValues::*;

	attribute def TurnKeyToOn;
	attribute def BrakePressure;

	action def MonitorBrakePedal { out attribute pressure : BrakePressure; }
	action def MonitorTraction { out attribute modFreq : Real; }
	action def Braking { in attribute brakePressure : BrakePressure; in attribute modulationFrequency : Real; }

	action def Brake {
	  first startBrake;
		action TurnOn {first firstTurnOn; then done;}
		then forkNode;
		fork forkNode;
			then monitorBrakePedal;
			then monitorTraction;
			then braking;


		action monitorBrakePedal : MonitorBrakePedal {
			out attribute brakePressure;
			first start;
			then done;
		}
		then joinNode;

		action monitorTraction : MonitorTraction {
			out attribute modulationFrequency;
			first start;
    	then done;
		}
		then joinNode;

		flow from monitorBrakePedal.brakePressure to braking.brakePressure;
		flow from monitorTraction.modulationFrequency to braking.modulationFrequency;

		action braking : Braking {
			in attribute brakePressure;
			in attribute modulationFrequency;
			first start;
      then done;
		}
		then joinNode;

		join joinNode;
		then done;
	}
}
