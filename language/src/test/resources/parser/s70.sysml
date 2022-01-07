port def ClutchPort;
action def GenerateTorque;

requirement def id '2' DrivePowerInterfaceRequirement {
  doc /* The engine shall transfer its generated torque to the transmission
       * via the clutch interface. */
  subject clutchPort: ClutchPort;
}

requirement def id '3' TorqueGenerationRequirement {
  doc /* The engine shall generate torque as a function of RPM as shown in Table 1. */
  subject generateTorque: GenerateTorque;
}
