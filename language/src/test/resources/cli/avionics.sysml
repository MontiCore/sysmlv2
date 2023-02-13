
/**
* Example MBSE4U Blogpost: What's inside the SysML v2 Releases
**/
package Items {

        item def ArmingSignal;
    item def DrogueSignal specializes Signal;
    item def MainSignal specializes Signal;
    item def Signal;



}


package AvionicsParts {
    import Items::*;
    import ScalarValues::*;
    port def VoltagePort{
        value : Real;
    }
    port def DataOutPort{
        value : Real;
    }

    port def ParachutePort{
        value : Boolean;
    }

    part def RecoveryBoard{
        in port drogue : ParachutePort;
        in port main : ParachutePort;

        in port voltageIn : VoltagePort;
    }

    part def COTSRecovery specializes RecoveryBoard;
    part def SRADRecovery specializes RecoveryBoard{

        part def Sensor{
         in port dataOut : DataOutPort;
        }
        part def IMU specializes Sensor {
            in port zAcc : DataOutPort;
            in port zSpeed : DataOutPort;
        }
        part def Memory{//braucht man das??
         in port logData : DataOutPort;
        }
        part Microcontroller{
            in port voltageIn : VoltagePort;
            in port pressData : DataOutPort;
            in port zAcc : DataOutPort;
            in port zSpeed : DataOutPort;
            exhibit state flightHandler {
                            in height : Real;
                            in zSpeed : Real;
                            in zAcceleraction : Real;
                            out drogue : ParachutePort;
                            out main : ParachutePort;
                            entry;
                            then Idle;
                            state Idle;
                            transition Idle
                            if zAcceleraction >= 10
                            then Ascend;

                            state Ascend;
                            transition Ascend
                            if zAcceleraction <= 0
                                then Coasting;
                            state Coasting;

                            transition Coasting
                            if zSpeed <= 0
                                do send true to drogue
                                then Descend;
                            state Descend;
                            transition Descend
                            if height <= 400
                                do send true to main
                                then Landing;
                            state Landing;

                            transition Landing
                            if zSpeed == 0 //Landung
                                then PostFlight;
                            state PostFlight;

                        }
            out port logData : DataOutPort;

            out port main : ParachutePort;
            out port drogue : ParachutePort;
            bind pressData.value = flightHandler.height;
            bind zAcc.value = flightHandler.zAcceleraction;
            bind zSpeed.value = flightHandler.zSpeed;
        }

        part ESP32 :> Microcontroller;
        part pressureSensor : Sensor; //vereinfachung: gibt höhe in metern an, ein wert
        part imuSensor : IMU; //vereinfachung: gibt beschleunigung auf der z achse an, und geschwindigkeit auf z achse an jeweils ein wert
        part sdCard : Memory;


        bind pressureSensor.dataOut = ESP32.pressData;
        bind imuSensor.zAcc = ESP32.zAcc;
        bind imuSensor.zSpeed = ESP32.zSpeed;
        bind ESP32.logData = sdCard.logData;
        bind voltageIn = ESP32.voltageIn;
        bind ESP32.drogue = drogue;
        bind ESP32.main = main;

    }
    part def PowerSystem{

        part def Battery{
            out port batteryVoltage : VoltagePort; //voltage und energie
        }
        part battery : Battery [2];

    }
    part def PyroSystem{
        in port drogueSignal : ParachutePort;
        in port mainSignal : ParachutePort;


        part def Parachute;
        part main : Parachute;
        part drogue : Parachute;
    }

    part def RecoverySystem {
        part cotsRec : COTSRecovery;
        part sradRec : SRADRecovery;
        part powerSys : PowerSystem;
        part pyroSys : PyroSystem;

        flow from powerSys.battery.batteryVoltage to sradRec.voltageIn;
        flow from powerSys.battery.batteryVoltage to cotsRec.voltageIn;

        flow from cotsRec.main to pyroSys.mainSignal;
        flow from cotsRec.drogue to pyroSys.drogueSignal;

        flow from sradRec.main to pyroSys.mainSignal;
        flow from sradRec.drogue to pyroSys.drogueSignal;
    }
    part system : RecoverySystem;

}
