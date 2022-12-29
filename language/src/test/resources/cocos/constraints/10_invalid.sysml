/* (c) https://github.com/MontiCore/monticore */
attribute someAttribute: boolean;

part def LogicBasedConstraints {
  // tests resolving of shadowed attributes
  attribute def someAttribute: int;

  constraint streams {
    someAttribute == true
  }
}
