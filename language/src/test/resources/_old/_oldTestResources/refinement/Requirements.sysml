/**
 * Requirements untereinander verfeinern
 */
package requirementRefinement {

  part def Dummy {
    value actual: boolean;
  }

  requirement def Rough (desired: boolean) {
    //TODO subject dummy: Dummy;

    require constraint { dummy.actual == desired || dummy.actual != desired }
  }

  // die Paramter müssen gleich sein
  requirement def Refined(b: boolean) refines Rough {

    //subject dummy: Dummy;

    require constraint { dummy.actual == desired }
  }

  // Siehe 'refinement/Constraints.sysml' für weitere Fragen zu Instanzen, etc.

}
