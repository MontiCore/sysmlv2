package ShadowingScopeOfPart {
  part def Engine;
  part engine: Engine;

  part def Vehicle {
    // this engine should shadow engine defined outside this scope
    part engine: Engine;
  }

  part vehicle {
    // this engine should shadow engine defined outside this scope
    part engine: Engine;
  }
}
