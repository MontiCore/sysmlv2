port def IntPort {
  attribute flow: int;
}

part tank {
  port inPort: IntPort;
  port outPort: ~IntPort;

  connect inPort.flow to outPort.flow;
}
