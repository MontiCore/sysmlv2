package automata {
  port def BooleanInput {
    in attribute val: Boolean;
  }

  port def BooleanOutput {
    out attribute val: Boolean;
  }

  part def Inverter {
    port i: BooleanInput;
    port o: BooleanOutput;

    exhibit state behavior: automata.AutomatonInverter(i = i::val, o = o::val);
  }
}
