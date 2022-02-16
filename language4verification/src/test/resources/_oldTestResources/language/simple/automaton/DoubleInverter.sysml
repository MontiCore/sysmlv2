package automaton {

  part def DoubleInverter {
    port input : BooleanInput;
    port output : BooleanOutput;

    part inverter1 : Inverter;
    part inverter2 : Inverter;

    connect input to inverter1::i;
    connect inverter1::o to inverter2::i;
    connect inverter2::o to output;
  }
}
