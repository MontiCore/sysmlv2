package avionic {

  port def Input {
    in attribute val: Boolean;
  }

  port def Output {
    out attribute val: Boolean;
  }

  /**
   * Basis
   */
  part def UnfairMedium {
    port i1: Input;
    port o1: Output;

    exhibit state behavior {
      entry; then State;

      state State;

      transition Name1
        first State
        accept i1.val
        do action { send i1.val to o1.val; }
        then State;

      transition Name2
        first State
        accept i1.val
        then State;
    }
  }
}
