enum def CtrlEnum {
  enum ACK;
  enum NAK;
}
port def Ctrl {
  out attribute data: CtrlEnum;
}
part def UsingCtrlEnum {

  port control: ~Ctrl;

  require constraint constraintWithCtrlEnum {
    CtrlEnum.NAK == CtrlEnum.ACK
  }
}
