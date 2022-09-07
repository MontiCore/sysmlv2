port def IntPort {
  attribute flow: Integer;
}

part tank {
  port inPort: IntPort;
  port outPort: ~IntPort;

  connect inPort to outPort;
}
