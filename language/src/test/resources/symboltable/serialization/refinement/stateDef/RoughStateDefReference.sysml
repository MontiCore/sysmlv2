/* (c) https://github.com/MontiCore/monticore */
package RoughStateDefReference {

  port def Input {
    in attribute val: boolean;
  }

  port def Output {
    out attribute val: boolean;
  }

  /**
   * Transitions-Verfeinerung
   */
  part def PerfectMedium :> RoughStateDef.UnfairMedium {
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
