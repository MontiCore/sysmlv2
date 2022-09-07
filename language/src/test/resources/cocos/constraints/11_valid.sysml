attribute someAttribute: Integer;

part def LogicBasedConstraints {
  // tests resolving of shadowed attributes
  attribute def someAttribute: Boolean;

  constraint streams {
    someAttribute == true
  }
}
