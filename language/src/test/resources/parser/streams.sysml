port def Integers {
  out attribute channel: Integer;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Integers;

  assert constraint streams {
    #input.channel > output.channel.get(5)
  }
}
