package refinement {
  // B comes from the same package, in the future we should allow B to be imported
  part def A :> refinement.B {
    port input: BooleanInput;
    port output: BooleanOutput;

    assert constraint {
      input == output
    }

    // forces refinement by construction
    assert constraint {
      true
    }
  }
}
