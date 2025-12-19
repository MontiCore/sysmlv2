requirement 'Requirement' {
  subject Inverter_LLR1;

  assert constraint {
    input.val = <a, Tick> implies output.val = <!a, Tick>
  }
}
