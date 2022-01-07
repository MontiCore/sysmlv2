package a {
  import ISQ::*;
  import SI::*;
  import ScalarFunctions::*;

  constraint def MassConstraint {
    attribute partMasses : MassValue[0..*];
    attribute massLimit : MassValue;
    sum(partMasses) <= massLimit
  }

  part def Vehicle {
    constraint massConstraint : MassConstraint {
      partMasses redefines partMasses = {chassisMass, engine::mass, transmission::mass};
      massLimit redefines massLimit = 2500;
    }

    attribute chassisMass : MassValue;

    part engine : Engine {
      attribute mass : MassValue;
    }

    part transmission : Engine {
      attribute mass : MassValue;
    }
  }
}
