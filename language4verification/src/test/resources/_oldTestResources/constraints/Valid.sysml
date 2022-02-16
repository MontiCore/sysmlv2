package constraints {
  import verification.DefaultTypes;

  port def InputBoolean {
    in val: Boolean;
  }

  port def OutputBoolean {
    out val: Boolean;
  }

  part def Valid  {
    port i: InputBoolean;
    port o: OutputBoolean;

    value v = i::nth;

    assert constraint {
      v(0) == i::nth(0)
    }
  }
}
