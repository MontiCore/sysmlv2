port def Fluid {
  attribute fluidFlow: Integer[*];
}

part tank {
  port lubrication: Fluid[1..*];
  port filtering: Fluid[4];
}

part specification {
  port filtering: Fluid[4];

  assert constraint expressions {
    filtering[2] == filtering[3] + 1
  }
}
