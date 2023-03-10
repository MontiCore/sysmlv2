/* (c) https://github.com/MontiCore/monticore */
part def LogicBasedConstraints {

  constraint streams {
    exists i in {1,2,3,4}:
      i < 1
  }
}
