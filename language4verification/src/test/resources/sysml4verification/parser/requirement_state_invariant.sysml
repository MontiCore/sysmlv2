package RequirementStateInvariant {

  part def StateInvariant {
    state behavior: MyAutomaton();
  }

  state def MyAutomaton() {
    entry; then State1;

    state State1;
    state State2;

    value iTest: int = 0;

    transition t1
      first State1
      then State2;
  }

  requirement def MyStateInvariant {
    subject myAut: MyAutomaton;

    require constraint {
      myAut.iTest < 10
    }
  }
}
