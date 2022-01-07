part def Vehicle {
  attribute dryMass: MassValue;
  attribute fuelMass: MassValue;
  attribute fuelFullMass: MassValue;
}

requirement def id '1' VehicleMassLimitationRequirement :> MassLimitationRequirement {
  doc /* The total mass of a vehicle shall be less than or equal to the required mass. */

  subject vehicle : Vehicle;

  attribute myMassActual redefines massActual = vehicle::dryMass + vehicle::fuelMass;

  assume constraint { vehicle::fuelMass > 0 }
}
