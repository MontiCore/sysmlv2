port def IntPort {
  attribute flow: Integer;
}

port def StringPort {
  attribute flow: String;
}

part tank {
  port intPort: IntPort;
  port stringPort: ~StringPort;

  connect intPort to stringPort;
}
