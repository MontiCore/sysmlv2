/* (c) https://github.com/MontiCore/monticore */
port def Integers {
  out attribute data: int;
}

part def LogicBasedConstraints {
  port input: int;

  constraint streams {
    input::data == true
  }
}
