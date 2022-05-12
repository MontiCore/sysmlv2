package avionic {

  port def Input {
    in val: Boolean;
  }

  port def Output {
    out val: Boolean;
  }

  /**
   * Basis
   */
  part def UnfairMedium {
    port i1: Input;
    port o1: Output;

    exhibit state behavior: UnfairMediumAutomaton(i = i1::val, o = o1::val);
  }

  state def UnfairMediumAutomaton(in i: Boolean, out o: Boolean) {
    entry; then State;

    state State;

    transition
      first State
      accept i
      do action { send i to o; }
      then State;

    transition
      first State
      accept i
      then State;
  }

}
