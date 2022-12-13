port def Integers {
  out attribute channel: int;
}

part def LogicBasedConstraints {
  port input: ~Integers;

  constraint streams {
    input.channel.length() > true
  }
}
