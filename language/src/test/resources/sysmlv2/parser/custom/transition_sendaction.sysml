state def Automaton {

  state S;

  transition single_sendusage
    first S
    do send some.val + 1 to myport
    then T;

  transition nested_sendusage
    first S
    do action { send x to y; send s to t; }
    then T;

  state T;

}

package avionic {
  /* ---------------- SIDE ---------------- */
  state def GuidanceSystemAutomaton(in switch: Boolean, in input: Integer, out output: Integer) {
    entry; then S;
    state S;
    transition
      first S
      accept input
      then S;
  }
  port def SwitchPort {
    in val: Boolean;
  }
  part def GuidanceSystem {
    port switch: SwitchPort;
    port input: StatusInputPort;
    port output: StatusOutputPort;
    state behavior: GuidanceSystemAutomaton(switch = switch.val, input = input.val, output = output.val);
  }
  /* ---------------- BUS ---------------- */
  state def BusAutomaton(in input: Integer, out output: Integer) {
    entry; then S;
    state S;
    transition
      first S
      accept input
      do action { send input to output; }
      then S;
    // Unfair: Do nothing, i.e., swallow messages (maybe forever)
    transition
      first S
      accept input
      then S;
  }
  port def StatusInputPort {
    in val: Integer;
  }
  port def StatusOutputPort {
    out val: Integer;
  }
  part def Bus {
    port input: StatusInputPort;
    port output: StatusOutputPort;
    state behavior: BusAutomaton(input = input.val, output = output.val);
  }
  /* ---------------- PFS ---------------- */
  part def PFS {
    port switch: SwitchPort;
    port output1: StatusOutputPort;
    port output2: StatusOutputPort;
    part leftSide: GuidanceSystem;
    part bus1: Bus;
    part rightSide: GuidanceSystem;
    part bus2: Bus;
    connect switch to leftSide.switch;
    connect switch to rightSide.switch;
    connect leftSide.output to bus1.input;
    connect bus1.output to rightSide.input;
    connect rightSide.output to bus2.input;
    connect bus2.output to leftSide.input;
    connect bus1.output to output1;
    connect bus2.output to output2;
  }
  /* ---------------- FAIR BUS ---------------- */
  state def FairBusAutomaton(in input: Integer, out output: Integer) {
    entry
      action { send 10 to restrictor; }
      then S;
    value restrictor: Integer;
    state S;
    transition
      first S
      accept input
      do action { send input to output; send 10 to restrictor; }
      then S;
    // Fair: Do nothing, i.e., swallow messages (but not forever)
    transition
      first S
      accept input
      if restrictor != 0
      do action { send restrictor-1 to restrictor; }
      then S;
  }
  part def FairBus refines avionic.Bus {
    port input: StatusInputPort;
    port output: StatusOutputPort;
    state behavior: FairBusAutomaton(input = input.val, output = output.val);
  }
  /* ---------------- FAIR PFS ---------------- */
  part def FairPFS {
    port switch: SwitchPort;
    port output1: StatusOutputPort;
    port output2: StatusOutputPort;
    part leftSide: GuidanceSystem;
    part bus1: FairBus;
    part rightSide: GuidanceSystem;
    part bus2: FairBus;
    connect switch to leftSide.switch;
    connect switch to rightSide.switch;
    connect leftSide.output to bus1.input;
    connect bus1.output to rightSide.input;
    connect rightSide.output to bus2.input;
    connect bus2.output to leftSide.input;
    connect bus1.output to output1;
    connect bus2.output to output2;
  }
}
