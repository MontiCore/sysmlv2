/* (c) https://github.com/MontiCore/monticore */
port def Integers {
  out attribute natAttr: int;
}

part def LogicBasedConstraints {
  port myPortDef: Integers;

  constraint constraintName {
    myPortDef.natAttr.nth(3).nth(3) > myPortDef.nth(3).nth(3)
  }
}
