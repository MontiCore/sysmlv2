
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
        in value : Real;
    }
    port def DataOutPort{
        out value : Real;
    }

    port def ParachutePort{
        out value : Boolean;
    }

    part def RecoveryBoard{
        port drogue : ParachutePort;
        port main : ParachutePort;

        //port voltageIn : VoltagePort; //das vlt weg
    }

    part def COTSRecovery specializes RecoveryBoard;
    part def SRADRecovery specializes RecoveryBoard{

        part def Sensor{ //weird
         port dataOut : DataOutPort;
        }
        part def IMU specializes Sensor {
            port zAcc : DataOutPort;
            port zSpeed : DataOutPort;
        }
        part def Memory{
         port logData : DataOutPort; //sinnvoll zu modellieren?
        }
        part Microcontroller{
            //port voltageIn : VoltagePort; //?
            port pressData : ~DataOutPort;
            port zAcc : ~DataOutPort;
            port zSpeed : ~DataOutPort;
            exhibit state flightHandler {
                            in height : Real = pressData.value;
                            in zSpeed : Real = zSpeed.value;
                            in zAcceleraction : Real = zAcc.value;
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
                                do action send_0 send true to drogue //TODO reset to short notation
                                then Descend;
                            state Descend;
                            transition Descend
                            if height <= 400
                                do action send_1 send true to main
                                then Landing;
                            state Landing;

                            transition Landing
                            if zSpeed == 0 //Landung
                                then PostFlight;
                            state PostFlight;

                        }
            port logData : DataOutPort;

            port main : ParachutePort;
            port drogue : ParachutePort;
        }

        part pressureSensor : Sensor; //vereinfachung: gibt höhe in metern an, ein wert
        part imuSensor : IMU; //vereinfachung: gibt beschleunigung auf der z achse an, und geschwindigkeit auf z achse an jeweils ein wert
        part sdCard : Memory;

//Durch flows ersetzen
        bind pressureSensor.dataOut = Microcontroller.pressData;
        bind imuSensor.zAcc = Microcontroller.zAcc;
        bind imuSensor.zSpeed = Microcontroller.zSpeed;
        bind Microcontroller.logData = sdCard.logData;
        //bind voltageIn = Microcontroller.voltageIn;
        bind Microcontroller.drogue = drogue;
        bind Microcontroller.main = main;

    }
    part def PowerSystem{

        part battery{
            port batteryVoltage : ~VoltagePort; //voltage und energie
        }

    }
    part def PyroSystem{
        port drogueSignal : ~ParachutePort;
        port mainSignal : ~ParachutePort;


        part def Parachute;
        part main : Parachute;
        part drogue : Parachute;
    }

    part def RecoverySystem {
        part cotsRec : COTSRecovery;
        part sradRec : SRADRecovery;
        part powerSys : PowerSystem;
        part pyroSys : PyroSystem;

        //flow from powerSys.battery.batteryVoltage to sradRec.voltageIn;
        //flow from powerSys.battery.batteryVoltage to cotsRec.voltageIn;

        flow from cotsRec.main to pyroSys.mainSignal;
        flow from cotsRec.drogue to pyroSys.drogueSignal;

        flow from sradRec.main to pyroSys.mainSignal;
        flow from sradRec.drogue to pyroSys.drogueSignal;
    }
    part system : RecoverySystem;

}
