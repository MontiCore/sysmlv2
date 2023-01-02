/* (c) https://github.com/MontiCore/monticore */
port def Integers {
  out attribute channel: int;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Integers;

  constraint streams {
    input::channel == output::channel
  }
}
