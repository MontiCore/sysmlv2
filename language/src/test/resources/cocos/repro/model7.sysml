package Repro0xB0166{
port def Booleans {
  in attribute val: boolean;
}

part def model2 {
  port input: Booleans;
  port output: ~Booleans;
}

requirement 'Requirement' {
  subject m2: model2;
  attribute a: boolean;

  assert constraint {
    m2.input.val == <a, Tick> implies true
  }
}
}
