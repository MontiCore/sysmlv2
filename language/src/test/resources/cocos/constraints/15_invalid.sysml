port def Booleans {
  in attribute val: boolean;
}

part def Inverter {
  port i: Booleans;
  port o: ~Booleans;

  assert constraint {
    forall nat k: i.val == <true>.times(k, 5) implies o.val == <false>.times(k)
  }
}
