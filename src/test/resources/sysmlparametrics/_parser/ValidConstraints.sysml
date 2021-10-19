package ValidConstraints {

  constraint constraintA {
    100 < 200
  }

  constraint {
     100 < 200
  }

  assert constraint constraintB {
    100 < 200
  }

  assert constraintC {
    100 < 200
  }

  assert {
    100 < 200
  }
}