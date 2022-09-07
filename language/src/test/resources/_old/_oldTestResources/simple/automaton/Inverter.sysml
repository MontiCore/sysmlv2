package automaton {

  port def BooleanInput {
    in val: Boolean;
  }

  port def BooleanOutput {
    out val: Boolean;
  }

  part def Inverter {
    port i: BooleanInput;
    port o: BooleanOutput;

    state behavior: AutomatonInverter(i = i::val, o = o::val);
  }

  state def AutomatonInverter(in i: Boolean, out o: Boolean){
    entry;
      then S;

    state S;

    transition invert
      first S
      if true
      do send !i to o
      then S;
  }
}
