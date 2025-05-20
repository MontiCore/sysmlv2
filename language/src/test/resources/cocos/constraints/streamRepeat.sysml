port def Booleans {
   in attribute val : boolean;
}

part def Inverter {
  port i : Booleans;
  port o : ~Booleans;

  requirement{
    assert constraint{
      forall nat k:   i.val == <true>.times(k)  implies  o.val == <false>.times(k)  }
  }
}
