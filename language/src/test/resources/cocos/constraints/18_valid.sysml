/* (c) https://github.com/MontiCore/monticore */
attribute def A {
  attribute a: int;
}
port def Integers {
  out attribute natAttr: A;
}

part def LogicBasedConstraints {
  port myPortDef: Integers;
  constraint constraintName {
    myPortDef.natAttr.nth(3).nth(2).a < myPortDef.natAttr.nth(3).nth(3).a
  }
}
