requirement vehicleSpecification {
  doc /* Overall vehicle requirements group */
  subject vehicle : Vehicle;

  require fullVehicleMassLimit;
  require emptyVehicleMassLimit;
}

part def Engine {
  port clutchPort: ClutchPort;
  perform action generateTorque: GenerateTorque;
}

requirement engineSpecification {
  doc /* Engine power requirements group */
  subject engine : Engine;

  requirement drivePowerInterface : DrivePowerInterfaceRequirement {
    subject clutchPort = engine::clutchPort;
  }

  requirement torqueGeneration : TorqueGenerationRequirement {
    subject generateTorque = engine::generateTorque;
  }
}
