/* (c) https://github.com/MontiCore/monticore */
package ForkJoinExample {
  import ScalarValues::*;

  attribute def TurnKeyToOn;
  attribute def BrakePressure;

  action def MonitorBrakePedal { out attribute pressure : BrakePressure; }
  action def MonitorTraction { out attribute modFreq : Real; }
  action def Braking { in attribute brakePressure : BrakePressure; in attribute modulationFrequency : Real; }

  action def Brake {
    first start;
    then TurnOn;
    action TurnOn {first start; then done;}
    then forkNode;
    fork forkNode;
      then monitorBrakePedal;
      then monitorTraction;
      then braking;


    action monitorBrakePedal : MonitorBrakePedal {
      out attribute brakePressure : Integer;
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

    action braking : Braking {
      in attribute brakePressure = monitorBrakePedal.brakePressure;
      in attribute modulationFrequency = monitorTraction.modulationFrequency;
      first start;
      then done;
    }
    then joinNode;

    join joinNode;
    then done;
  }
}
