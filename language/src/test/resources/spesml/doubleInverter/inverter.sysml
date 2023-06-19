part def inverter refines inverterSpec {
  port input : bool_port;
  port output : ~bool_port;

  exhibit state InverterAutomaton {
    state def StateInitial;
    transition invert
      first StateInitial
      if true
      do action {send !(input.in_dinv) to output.in_dinv;}
      then StateInitial;
  }
}
