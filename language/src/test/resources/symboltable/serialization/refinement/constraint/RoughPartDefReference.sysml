package refinement {
  // B comes from the same package, in the future we should allow B to be imported
  part def A refines refinement.B {
    port input: BooleanInput;
    port output: BooleanOutput;

    assert constraint SomeName1 {
      input == output
    }

    // forces refinement by construction
    assert constraint SomeName2 {
      true
    }
  }
}
