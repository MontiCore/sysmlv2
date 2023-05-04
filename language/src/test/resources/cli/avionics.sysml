package AvionicsParts {
    import ScalarValues::*;
    port def DataPort{
        out value : Real;
    }

    port def ParachutePort{
        out value : Boolean;
    }

    part def FlightComputer {
        port drogue : ParachutePort;
        port main : ParachutePort;
        part pressureSensor{
         port altitude : DataPort;
        }
        part IMU{
            port acceleration : DataPort;
            port velocity : DataPort;
        }
        part esp32 : Microcontroller;
        flow pressureSensor.altitude to esp32.altitude;
        flow IMU.acceleration to esp32.acceleration;
        flow IMU.velocity to esp32.velocity;
        flow esp32.drogue to drogue;
        flow esp32.main to main;
    }

    part def Microcontroller{
        port altitude : ~DataPort;
        port acceleration : ~DataPort;
        port velocity : ~DataPort;
        port main : ParachutePort;
        port drogue : ParachutePort;
        action sendFalse {
            first start;
            then send false to main;
            then send false to drogue;
            then done;
        }
        exhibit state flightHandler {
            attribute altitude : Real;
            attribute velocity : Real;
            attribute acceleraction : Real;
            do sendFalse;
            bind altitude = altitude.value;
            bind velocity = velocity.value;
            bind acceleraction = acceleration.value;
            then Idle;
            state Idle;
            transition Idle
              if acceleraction >= 10
              then Ascend;
            state Ascend;
            transition Ascend
              if acceleraction <= 0
              then Coasting;
            state Coasting;
            transition Coasting
              if velocity <= 0
              do send true to drogue
              then Descend;
            state Descend;
            transition Descend
              if altitude <= 250
              do send true to main
              then Landing;
            state Landing;
            transition Landing
              if velocity <= 10 && altitude <= 10
              then PostFlight;
            state PostFlight;
        }
    }

    part RecoverySystem {
        part RecoveryComputer : FlightComputer;
        part PyroSystem{
                     port drogueSignal : ~ParachutePort;
                     port mainSignal : ~ParachutePort;
        }

        flow from RecoveryComputer.main to PyroSystem.mainSignal;
        flow from RecoveryComputer.drogue to PyroSystem.drogueSignal;
    }
}
