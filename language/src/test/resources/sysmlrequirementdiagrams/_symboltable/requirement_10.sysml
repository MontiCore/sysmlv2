package Requirement_10 {
  part def Vehicle;
  part def Car :> Vehicle;

  // no parameters
  requirement def ReqDef1;

  // specialization with no inherited and new parameters
  requirement def ReqDef2 :> ReqDef1;

  // specialization with new parameters
  requirement def ReqDef3 :> ReqDef1(a1: Vehicle, a2: Vehicle, a3: Vehicle);

  // specialization with all redefinition
  requirement def ReqDef4 :> ReqDef3(b1: Car, b2: Vehicle, b3: Car);

  // specialization with partial redefinition
  requirement def ReqDef5 :> ReqDef3(c1: Vehicle);
  requirement def ReqDef6 :> ReqDef3(c1: Vehicle, c2: Car);

  // specialization with no redefinition, all params inherited
  requirement def ReqDef7 :> ReqDef3;

  // multiple generalizations with no parameters
  requirement def ReqDef8;
  requirement def ReqDef9 :> ReqDef1, ReqDef8;

  // multiple generalizations with new parameters
  requirement def ReqDef16 :> ReqDef1, ReqDef8(a1: Vehicle, a2);

  // multiple generalizations with one with parameters
  requirement def ReqDef10 :> ReqDef3, ReqDef8(b1: Car, b2: Car, b3: Car);

  // multiple generalizations with equal no. of parameters
  requirement def ReqDef11(a1, a2: Vehicle, a3);
  requirement def ReqDef12 :> ReqDef3, ReqDef11 (b1: Car, b2: Vehicle, b3: Vehicle);
  requirement def ReqDef13 :> ReqDef3, ReqDef11 (b1: Vehicle, b2: Vehicle, b3: Vehicle, b4);

  // multiple generalizations with different no. of parameters
  requirement def ReqDef14 :> ReqDef16, ReqDef11 (b1: Vehicle, b2: Car, b3: Vehicle);
  requirement def ReqDef15 :> ReqDef16, ReqDef11 (b1: Vehicle, b2: Vehicle, b3: Vehicle, b4: Vehicle);
}
