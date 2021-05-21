action def Brake {
  action TurnOn;
  then fork;
    then monitorBrakePedal;
    then monitorTraction;
    then braking;

  action monitorBrakePedal : MonitorBrakePedal {
    out brakePressure; }
  then joinNode;

  action monitorTraction : MonitorTraction {
    out modulationFrequency; }
  then joinNode;

  action braking : Braking {
    in brakePressure stream from
      monitorBrakePedal::brakePressure;
    in modulationFrequency stream from
      monitorTraction::modulationFrequency;}
  then joinNode;

  join joinNode;
  then done;
}
