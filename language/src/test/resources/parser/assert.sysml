port def Integers {
  out attribute channel: Integer;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Integers;

  assert constraint childsPlay {
    (1+1==2) implies false
  }

  assert constraint behavior {
    input.channel == output.channel
  }
}
