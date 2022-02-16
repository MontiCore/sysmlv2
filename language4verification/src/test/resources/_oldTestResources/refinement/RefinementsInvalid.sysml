/**
 * Einzelne Constraints untereinander verfeinern
 */
package invalidRefinement {

  // Wrong type constraint refinement
  constraint def Refined (b: int) refines Rough {
    b == 5
  }
  constraint def Rough(b: boolean) {
    b == true || b == false
  }

  // Wrong type state refinement
  state def Terminator (b : boolean, i : int) refines RoboCop {
    entry;
      then S;
  }
  state def RoboCop (b : boolean, i : boolean){
    entry;
      then S;
  }

  // Too many parameters requirement refinement
  requirement def RefinedReq (b: boolean, o : int) refines RoughReq {
      require constraint { dummy.actual == desired }
  }
  requirement def RoughReq (desired: boolean) {
    require constraint { dummy.actual == desired || dummy.actual != desired }
  }

  // Wrong general definition (constraint but should be requirement)
  requirement def WrongDefinition (b: boolean) refines Rough {
    require constraint { dummy.actual == desired }
  }
}
