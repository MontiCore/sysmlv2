port def Double {
  out attribute channel: Double;
}

part def LogicBasedConstraints {
  port input: ~Double;

  constraint streams {
    input.channel.snth(5) > 1
  }
}
