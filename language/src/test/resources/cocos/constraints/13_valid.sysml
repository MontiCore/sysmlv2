port def Integers {
  out attribute channel: Integer;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Integers;

  constraint streams {
    input::channel == output::channel
  }
}
