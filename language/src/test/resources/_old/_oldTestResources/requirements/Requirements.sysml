/**
 * Requirements untereinander verfeinern
 */
package requirementRefinement {
  part def Dummy {
    value actual: boolean;
  }

  requirement def Rough (desired: boolean) {
    subject dummy: Dummy;
    require constraint { dummy.actual == desired || dummy.actual != desired }
  }

  requirement def Refined(b: boolean) refines Rough {
    subject dummy: Dummy;
    require constraint { dummy.actual == desired }
  }
}
