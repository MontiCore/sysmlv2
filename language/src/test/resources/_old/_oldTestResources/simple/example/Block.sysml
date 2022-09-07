package example {

  part def BlockA {
    port i: BooleanInput;

    part def BlockB;
  }

  package example2 {
    part def BlockC {
      port j: BooleanInput2;
      port k: BooleanInput3;
    }
  }

}
