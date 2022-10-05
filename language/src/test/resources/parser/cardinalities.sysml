port def Fluid {
  attribute flow: Integer[*];
}

part tank {
  port lubrication: Fluid[1..*];
  port filtering: Fluid[4];
}
