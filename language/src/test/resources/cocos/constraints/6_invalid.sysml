port def Integers {
  out attribute data: Integer;
}

part def LogicBasedConstraints {
  port input: Integer;

  constraint streams {
    input::data == true
  }
}
