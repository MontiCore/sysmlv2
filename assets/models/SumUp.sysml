port def Naturals {
  in attribute val: nat;
}

part def SumUp {
  #time untimed;
  port x:  Naturals;
  port y: ~Naturals;

  assume constraint InfStream {
    x.hasInfiniteLen()
  }

  require constraint IncrementInplace {
    forall nat t:
      y.nth(t+1) == x.nth(t) + y.nth(t)
  }
}
