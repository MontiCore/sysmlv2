/* (c) https://github.com/MontiCore/monticore */
package RoughPartDefReference {
  // B comes qualified, in the future we should allow B to be imported
  part def A refines RoughPartDef.B {
    port input: RoughPartDef.BooleanInput;
    port output: RoughPartDef.BooleanOutput;

    assert constraint SomeName1 {
      input == output
    }

    // forces refinement by construction
    assert constraint SomeName2 {
      true
    }
  }
}
