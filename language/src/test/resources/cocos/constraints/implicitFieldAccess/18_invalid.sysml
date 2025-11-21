/* (c) https://github.com/MontiCore/monticore */
attribute def A {
  attribute a: boolean;
}
port def Integers {
  out attribute natAttr: A;
}

part def LogicBasedConstraints {
  port myPortDef: Integers;
  constraint constraintName {
    myPortDef.nth(3).nth(2).a < myPortDef.a.nth(3).nth(3)
  }
}
