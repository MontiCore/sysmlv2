package avionic {

  /**
   * Zustands-Verfeinerung
   */
  part def FairMedium refines avionic.UnfairMedium {
    port i: Input;
    port o: Output;

    state behavior: FairMediumAutomaton(i = i::val, o = o::val);
  }

  state def FairMediumAutomaton(in i: Boolean, out o: Boolean) {
    entry; then State;

    value counter: nat = 0;

    state State;

    transition
      first State
      accept i
      if counter == 0
      do action { send i to o; send 10 to counter; }
      then State;

    transition
      first State
      accept i
      if counter > 0
      do action { send counter-1 to counter; }
      then State;
  }

}
