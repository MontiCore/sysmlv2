part vehicle_c1 : Vehicle {
  part engine_v1: Engine { }
}

part 'Vehicle c1 Design Context' {
  ref vehicle_design :> vehicle_c1;

  satisfy vehicleSpecification by vehicle_design;
  satisfy engineSpecification by vehicle_design::engine_v1;
}