/* (c) https://github.com/MontiCore/monticore */
port def Integers {
  out attribute channel: int;
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
