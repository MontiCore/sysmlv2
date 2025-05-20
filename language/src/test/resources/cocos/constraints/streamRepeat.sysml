port def Booleans {
   in attribute val : boolean;
}

part def Inverter {
  port i : Booleans;
  port o : ~Booleans;

  requirement{
    assert constraint{
      forall nat k:   i.val == <true>.repeat(k)  implies  o.val == <false>.repeat(k)  }
  }
}
