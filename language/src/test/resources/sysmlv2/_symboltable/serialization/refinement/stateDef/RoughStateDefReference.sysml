package avionic {

  port def Input {
    in val: Boolean;
  }

  port def Output {
    out val: Boolean;
  }

  /**
   * Transitions-Verfeinerung
   */
  part def PerfectMedium :> avionic.UnfairMedium {
    port i: Input;
    port o: Output;

    state behavior: PerfectMediumAutomaton(i = i::val, o = o::val);
  }

  state def PerfectMediumAutomaton(in i: Boolean, out o: Boolean) {
    entry; then State;

    state State;

    transition
      first State
      accept i
      do action { send i to o; }
      then State;
    }
}
