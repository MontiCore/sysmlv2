/* (c) https://github.com/MontiCore/monticore */
port def Booleans {
  out attribute boolAttr: boolean;
}

part def LogicBasedConstraints {
  port myPortDef: Booleans;

  constraint constraintName {
    myPortDef.boolAttr.length() > 1
  }
}
