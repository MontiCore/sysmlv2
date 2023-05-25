part def inverter {
  port input : bool_port;
  port output : ~bool_port;

  satisfy requirement typeCheck  {
    constraint inputType {
      input.type() == bool_port
    }
    constraint outputType {
      output.type() == bool_port
    }
  }

  exhibit state behavior: InverterAutomaton(input = input::val, output = output::val);
}

state def InverterAutomaton(in input: bool_port, out output: ~bool_port) {
  state def StateInitial;
  transition invert
    first StateInitial
    if true
    do action {send !input to output;}
    then StateInitial;
}
