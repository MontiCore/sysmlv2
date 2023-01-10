
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
item def Voltage;
    import Items::*;
    import ScalarValues::*;
    port def VoltagePort{
        inout voltage : Voltage;
    }
    port def DataOutPort{
        out value : Real;
    }

    port def ParachutePort{
        out signal : Signal;
    }

    part def RecoveryBoard{
        port drogue : ParachutePort;
        port main : ParachutePort;

        port voltageIn : VoltagePort;
    }

    part def COTSRecovery specializes RecoveryBoard;
    part def SRADRecovery specializes RecoveryBoard{

        part def Sensor{
         port dataOut : DataOutPort;
        }
        part def IMU specializes Sensor {
            port zAcc : DataOutPort;
            port zSpeed : DataOutPort;
        }
        part def Memory{//braucht man das??
         port logData : ~DataOutPort;
        }
        part def Microcontroller{
            port voltageIn : VoltagePort;
            in pressData : ~DataOutPort;
            in zAcc : ~DataOutPort;
            in zSpeed : ~DataOutPort;
            exhibit state flightHandler;
            out logData : DataOutPort;

            out main : ParachutePort;
            out drogue : ParachutePort;
            bind pressData.value = flightHandler.height;
            bind zAcc.value = flightHandler.zAcceleraction;
            bind zSpeed.value = flightHandler.zSpeed;
        }

        part ESP32 : Microcontroller;
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
            out batteryVoltage : Voltage; //voltage und energie
        }
        part battery : Battery [2];

    }
    part def PyroSystem{
        port drogueSignal : ~ParachutePort;
        port mainSignal : ~ParachutePort;


        part def Parachute;
        part main : Parachute;
        part drogue : Parachute;
    }

    part def RecoverySystem {
        private port externalCharging;
        part cotsRec : COTSRecovery;
        part sradRec : SRADRecovery;
        part powerSys : PowerSystem;
        part pyroSys : PyroSystem;

        flow of Voltage from powerSys.battery.batteryVoltage to sradRec.voltageIn;
        flow powerSys.battery.batteryVoltage to cotsRec.voltageIn;

        flow cotsRec.main to pyroSys.mainSignal;
        flow cotsRec.drogue to pyroSys.drogueSignal;

        flow sradRec.main to pyroSys.mainSignal;
        flow sradRec.drogue to pyroSys.drogueSignal;
    }
    part system : RecoverySystem;

    state def FlightHandler;
	action def VehicleStartSignal;

    state flightHandler : FlightHandler {
        in height : Real;
        in zSpeed : Real;
        in zAcceleraction : Real;
        out drogue : ~ParachutePort;
        out main : ~ParachutePort;
        entry;
        then Idle;
        state Idle;
            if zAcceleraction >= 10
            then Ascend;

        state Ascend;

        if zAcceleraction <= 0
            then Coasting;
        state Coasting;

        accept VehicleStartSignal
        if zSpeed <= 0
            do send VehicleStartSignal() to drogue.signal;
            then Descend;
        state Descend;
        if height <= 400
            do send MainSignal() to main.signal;
            then Landing;
        state Landing;

        if zSpeed == 0 //Landung
            then PostFlight;
        state PostFlight;

    }

    port p1 : VoltagePort;

    port p2 : VoltagePort;

    port p3 : VoltagePort;

    port p4 : VoltagePort;
    interface a connect p1 to p2;

}
