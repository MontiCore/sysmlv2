package Requirement_10 {
  /*
    Requirements have been reordered to ensure order of elements does not
    have an effect on the implementation.
  */

  part def Vehicle;
  part def Car :> Vehicle;

  // specialization with partial redefinition
  requirement def ReqDef5 :> ReqDef3(c1: Vehicle);
  requirement def ReqDef6 :> ReqDef3(c1: Vehicle, c2: Car);

  // specialization with no redefinition, all params inherited
  requirement def ReqDef7 :> ReqDef3;

  // multiple generalizations with no parameters
  requirement def ReqDef8;
  requirement def ReqDef9 :> ReqDef1, ReqDef8;

  // no parameters
  requirement def ReqDef1;

  // specialization with no inherited and new parameters
  requirement def ReqDef2 :> ReqDef1;

  // specialization with new parameters
  requirement def ReqDef3 :> ReqDef1(a1: Vehicle, a2: Vehicle, a3: Vehicle);

  // specialization with all redefinition
  requirement def ReqDef4 :> ReqDef3(b1: Car, b2: Vehicle, b3: Car);

  // multiple generalizations with one with parameters
  requirement def ReqDef10 :> ReqDef3, ReqDef8(b1: Car, b2: Car, b3: Car);

  // multiple generalizations with equal no. of parameters
  requirement def ReqDef11(a1, a2: Vehicle, a3);
  requirement def ReqDef12 :> ReqDef3, ReqDef11 (b1: Car, b2: Vehicle, b3: Vehicle);
  requirement def ReqDef13 :> ReqDef3, ReqDef11 (b1: Vehicle, b2: Vehicle, b3: Vehicle, b4);

  // multiple generalizations with new parameters
  requirement def ReqDef16 :> ReqDef1, ReqDef8(a1: Vehicle, a2);

  // multiple generalizations with different no. of parameters
  requirement def ReqDef14 :> ReqDef16, ReqDef11 (b1: Vehicle, b2: Car, b3: Vehicle);
  requirement def ReqDef15 :> ReqDef16, ReqDef11 (b1: Vehicle, b2: Vehicle, b3: Vehicle, b4: Vehicle);

  // required redefinition of inherited parameters when specializing multiple requirement definitions
  requirement def ReqDef17(a: Vehicle, b: Vehicle, c: Vehicle); // originally 3 params
  requirement def ReqDef18 :> ReqDef17(a: Car, b: Car);         // reduced to 2 params, 1 inherited
  requirement def ReqDef19 :> ReqDef18(a: Car);                 // reduced to 1 param, 2 inherited
  requirement def ReqDef20 :> ReqDef18();                       // reduced to 0 params, 3 inherited
  requirement def ReqDef21 :> ReqDef18;                         // reduced to 0 params, 3 inherited
  requirement def ReqDef22(a: Car);

  /*
  ReqDef21 defines no new params, but has 3 inherited params
  ReqDef22 defined a single param
  ReqDef23 should redefine 3 params, it can then have its own new params
  */
  requirement def ReqDef23:> ReqDef21, ReqDef22(a: Car, b: Car, c: Car, d: double);

  // ReqDef24 only redefines a single param, all other params are inherited
  requirement def ReqDef24:> ReqDef23(a: Car);
}
