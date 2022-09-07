port def Integers {
  out attribute channel: Integer;
}

part def LogicBasedConstraints {
  port input: ~Integers;

  constraint streams {
    input.channel.get(5) > 1
  }
}
