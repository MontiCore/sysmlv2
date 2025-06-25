port def Booleans {
  in attribute val: boolean;
}

part def Inverter {
  port i: Booleans;
  port o: ~Booleans;

  assert constraint {
    forall nat k: <true>.times(k).takes(k) == <1>.infTimes().takes(k)
  }
}
