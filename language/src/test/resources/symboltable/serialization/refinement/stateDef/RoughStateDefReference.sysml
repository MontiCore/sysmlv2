package avionic {

  port def Input {
    in attribute val: Boolean;
  }

  port def Output {
    out attribute val: Boolean;
  }

  /**
   * Transitions-Verfeinerung
   */
  part def PerfectMedium :> avionic.UnfairMedium {
    port i: Input;
    port o: Output;

    exhibit state behavior1 {
      entry; then State;

      state State;

      transition
        first State
        accept i.val
        do action { send i.val to o.val; }
        then State;
    }
  }
}
