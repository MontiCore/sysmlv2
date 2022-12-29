/* (c) https://github.com/MontiCore/monticore */
attribute someAttribute: int;

part def LogicBasedConstraints {
  // tests resolving of shadowed attributes
  attribute def someAttribute: boolean;

  constraint streams {
    someAttribute == true
  }
}
