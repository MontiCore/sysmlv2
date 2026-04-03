package Repro0xFD118{
port def Booleans {
  in attribute val: boolean;
}

part def model1 {
  port input: Booleans;
  port output: ~Booleans;
}

requirement 'Requirement' {
  subject model1;

  assert constraint {
    input.val == <a, Tick> implies true
  }
}
}
