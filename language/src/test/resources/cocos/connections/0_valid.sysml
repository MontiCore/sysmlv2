/* (c) https://github.com/MontiCore/monticore */
port def IntPort {
  attribute flow: int;
}

part tank {
  port inPort: IntPort;
  port outPort: ~IntPort;

  connect inPort to outPort;
}
