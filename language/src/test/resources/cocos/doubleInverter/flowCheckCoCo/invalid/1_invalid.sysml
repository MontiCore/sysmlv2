part def dinv {
  in attribute in_dinv: boolean;
  out attribute out_dinv: boolean;

  part inv1 : Inverter;
  part inv2 : Inverter;

  flow in_dinv to inv1.i;
  flow inv1.o to inv2.i;
  flow inv2.o to out_dinv;
}
