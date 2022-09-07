/**
 * State definitions verfeinern
 */
package statemachiensRefinement {

  state def RoboCop (b : boolean){
    entry;
      then S;
  }

  // die Paramter müssen gleich sein
  state def Terminator(b : boolean) refines RoboCop {
    entry;
      then S;
  }

  // Siehe 'refinement/Constraints.sysml' für weitere Fragen zu Instanzen, etc.

}
