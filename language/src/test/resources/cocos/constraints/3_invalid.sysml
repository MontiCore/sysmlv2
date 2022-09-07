port def Integers {
  out attribute channel: Integer;
}

port def Booleans {
  out attribute channel: Boolean;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Booleans;

  constraint streams {
    input.channel == output.channel
  }
}
