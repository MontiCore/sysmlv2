package specification {
  part def Inverter {
    port i : specification.BooleanInput;
    port o : specification.BooleanOutput;

    assert constraint {
      o == !i
    }
  }
}
