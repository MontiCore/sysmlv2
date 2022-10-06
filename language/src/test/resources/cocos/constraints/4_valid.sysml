port def Integers {
  out attribute channel: Integer;
}

part def LogicBasedConstraints {
  port input: ~Integers;

  constraint streams {
    input.channel.snth(5) > 1
  }
}
