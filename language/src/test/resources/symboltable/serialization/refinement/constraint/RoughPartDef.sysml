package refinement {
  part def B {
    port input:  BooleanInput;
    port output: BooleanOutput;

    assert constraint SomeName {
      true
    }
  }

  port def BooleanInput {
    in attribute val: Boolean;
  }

  port def BooleanOutput {
    out attribute val: Boolean;
  }
}
