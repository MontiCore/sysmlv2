requirement Repro0xB0166 {
  subject p : PartWithPort;
  require constraint {
    p.port1.val == true
  }
}

part def PartWithPort {
  port port1 : PortDef;
}

port def PortDef {
  attribute val : boolean;
}

part myPart {
  port myPort : PortDef;
  satisfy Repro0xB0166 {
    subject p = myPort;
  }
  require constraint {
    myPort.val == true
  }
}
