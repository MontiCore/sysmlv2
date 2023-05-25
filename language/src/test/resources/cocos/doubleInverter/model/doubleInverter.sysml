part def doubleinverter {
  part inv1 : inverter;
  part inv2 : inverter;
  port inputDoubleInv : bool_port;
  port outputDoubleInv : ~bool_port;

  satisfy requirement typeCheck  {
    constraint inv1Type {
      inv1.type() == inverter
    }
    constraint inv2Type {
      inv1.type() == inverter
    }
    constraint inputDoubleInvType {
      inputDoubleInv.type() == bool_port
    }
    constraint outputDoubleInvType {
      outputDoubleInv.type() == bool_port
    }
  }

  connect inputDoubleInv to inv1.input;
  connect inv1.output to inv2.input;
  connect inv2.output to outputDoubleInv.output;

  exhibit state behavior: DoubleInverterAutomaton(input = inputDoubleInv::val, output = outputDoubleInv::val);

}

state def DoubleInverterAutomaton(in input: bool_port, out output: ~bool_port){
  state def StateInitial;
  transition invert
    first StateInitial
    if true
    do action {
      send input to inv1.input ;
      send inv1.output to inv2.input;
      send inv2.output to output;
    }
    then StateInitial;
}
