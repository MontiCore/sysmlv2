port def Booleans {
  in attribute val: boolean;
}

part def Inverter {
  port i: Booleans;
  port o: ~Booleans;

  assert constraint {
    forall long k: i == <true>.times(k) implies o == <false>.times(k)
  }
}
