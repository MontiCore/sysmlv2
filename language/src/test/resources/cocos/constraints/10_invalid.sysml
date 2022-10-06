attribute someAttribute: Boolean;

part def LogicBasedConstraints {
  // tests resolving of shadowed attributes
  attribute def someAttribute: Integer;

  constraint streams {
    someAttribute == true
  }
}
