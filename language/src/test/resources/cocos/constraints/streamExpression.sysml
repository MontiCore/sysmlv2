port def Booleans {
   in attribute val : boolean;
}

part def Inverter {
  port i : Booleans;
  port o : ~Booleans;

  requirement{
    assert constraint{
      i.val == <true> implies o.val == <false> }
  }
}
