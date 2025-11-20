/* (c) https://github.com/MontiCore/monticore */
attribute def A {
  attribute a: boolean;
}

port def Integers {
  out attribute natAttr: int;
  //in attribute boolNested: A;
}

part def LogicBasedConstraints {
  port myPortDef: Integers;

    // this should be invalid
    //myPortDef.boolNested.a.nth(3).nth(3) > 1

    // this should be valid
        // myPortDef.boolNested.nth(3).nth(3).a > 1
        // also reactivate all other tests
        // write a test about cardinalities in constraint usages

// Make a Bug report with resolving.
  constraint constraintName {
    myPortDef.nth(3).nth(2) < myPortDef.natAttr.nth(3).nth(3)
  }
}
