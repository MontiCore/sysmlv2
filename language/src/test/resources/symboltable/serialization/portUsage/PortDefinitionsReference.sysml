package PortDefinitionsReference {
  part def Inverter {
    port i : PortDefinitions.BooleanInput;
    port o : PortDefinitions.BooleanOutput;

    assert constraint NamedConstraint {
      o == !i
    }
  }
}
