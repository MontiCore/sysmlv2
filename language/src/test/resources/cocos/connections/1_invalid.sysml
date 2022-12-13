port def IntPort {
  attribute flow: int;
}

port def StringPort {
  attribute flow: String;
}

part tank {
  port intPort: IntPort;
  port stringPort: ~StringPort;

  connect intPort.flow to stringPort.flow;
}
