package RoughPartDef {
  part def B {
    port input:  BooleanInput;
    port output: BooleanOutput;

    assert constraint SomeName {
      true
    }
  }

  port def BooleanInput {
    in attribute val: boolean;
  }

  port def BooleanOutput {
    out attribute val: boolean;
  }
}
