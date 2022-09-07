package typecheck {
  // this is used for testing the issue https://git.rwth-aachen.de/monticore/languages/sysml2/sysml4verification/-/issues/17
  // the test verifies that variables are resolvable inside constraint scope
  // as well the the type of sysml expressions can be resolved

  port def InputBoolean {
    in val: Boolean;
  }

  part def Valid  {
    port i: InputBoolean;

    value v = i::val::nth;

    assert constraint {
      v(0) == i::val::nth(0)
    }
  }
}
