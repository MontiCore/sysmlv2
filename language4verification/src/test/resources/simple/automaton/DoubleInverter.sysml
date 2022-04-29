package automaton {

  part def DoubleInverter {
    port input: BooleanInput;
    port output: BooleanOutput;

    part inverter1: Inverter;
    part inverter2: Inverter;

    connect input to inverter1::i;
    connect inverter1::o to inverter2::i;
    connect inverter2::o to output;
  }

  port def BooleanInput {
    in val: Boolean;
  }

  port def BooleanOutput {
    out val: Boolean;
  }

  part def Inverter {
    port i: BooleanInput;
    port o: BooleanOutput;

    exhibit state behavior: AutomatonInverter(i = i::val, o = o::val);
  }

  state def AutomatonInverter(in i: Boolean, out o: Boolean){
    entry;
      then S;

    state S;

    transition invert
      first S
      accept i
      if true
      do send !i to o
      then S;
  }
}
