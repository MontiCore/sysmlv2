package refinement {
  part def B {
    port input: BooleanInput;
    port output: BooleanOutput;

    assert constraint {
      true
    }
  }

  port def BooleanInput {
    in val: Boolean;
  }

  port def BooleanOutput {
    out val: Boolean;
  }
}
