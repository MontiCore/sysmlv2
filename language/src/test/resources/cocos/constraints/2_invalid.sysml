/* (c) https://github.com/MontiCore/monticore */
port def Integers {
  out attribute channel: int;
}

port def Booleans {
  out attribute channel: boolean;
}

part def LogicBasedConstraints {
  port input: ~Integers;
  port output: Booleans;

  constraint streams {
    input == output
  }
}
