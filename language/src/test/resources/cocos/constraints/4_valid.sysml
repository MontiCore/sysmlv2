port def Integers {
  out attribute natAttr: int;
}

part def LogicBasedConstraints {
  port myPortDef: Integers;

  constraint constraintName {
    myPortDef.natAttr.snth(5) > 1
  }
}
