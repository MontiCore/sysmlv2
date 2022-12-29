/* (c) https://github.com/MontiCore/monticore */
port def IntPort {
  attribute flow: int;
}

port def StringPort {
  attribute flow: String;
}

part tank {
  port intPort: IntPort;
  port stringPort: ~StringPort;

  connect intPort to stringPort;
}
