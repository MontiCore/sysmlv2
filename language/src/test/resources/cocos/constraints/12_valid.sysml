port def Double {
  out attribute channel: Double;
}

part def LogicBasedConstraints {
  port input: ~Double;

  constraint streams {
    input.channel.get(5) > 1
  }
}
