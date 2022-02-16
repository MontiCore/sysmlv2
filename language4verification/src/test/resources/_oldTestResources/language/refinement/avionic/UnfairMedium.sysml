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
    port i: Input;
    port o: Output;

    state behavior: UnfairMediumAutomaton(i = i::val, o = o::val);
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