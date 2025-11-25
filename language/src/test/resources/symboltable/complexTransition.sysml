
package myPackage {
  enum def MyEnum {
    A; B;
  }

  part def MyPartDef {
    attribute attr1 : MyEnum;
  }

  state def MyStates {
    in myInput : MyPartDef;
    entry;
    then stateA;

    state stateA { }
    state stateB { }
    transition t_a_b
      first stateA
      if myInput.attr1 == MyEnum::B
      then stateB;
  }
}
