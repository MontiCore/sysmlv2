constraint def MassConstraint (
  partMasses : MassValue[0..*],
  massLimit : MassValue);

constraint massConstraint : MassConstraint (
  partMasses : MassValue[0..*],
  massLimit : MassValue) {
  sum(partMasses) <= massLimit
}

part def Vehicle {
  assert massConstraint (
    partMasses = {chassisMass, engine::mass, transmission::mass},
    massLimit = 2500);

  attribute chassisMass : MassValue;

  attribute engine : Engine {
    value mass : MassValue;
  }

  attribute transmission : Engine {
    value mass : MassValue;
  }
}
