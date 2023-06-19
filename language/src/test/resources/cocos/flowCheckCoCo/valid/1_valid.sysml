part def dinv {
  in attribute in_dinv: boolean;
  out attribute out_dinv: boolean;

  part inv1 : Inverter;
  part inv2 : Inverter;

  connect in_dinv to inv1.i;
  connect inv1.o to inv2.i;
  connect inv2.o to out_dinv;
}
