/* (c) https://github.com/MontiCore/monticore */
port def Integers {
  out attribute channel: boolean;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Integers;

  assert constraint streams {
    input.channel.filter(e -> e) == <true>
  }
}
