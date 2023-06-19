part def doubleInverter refines doubleInverterSpec {
  part inv1 : inverter;
  part inv2 : inverter;
  port inputDoubleInv : bool_port;
  port outputDoubleInv : ~bool_port;

  connect inputDoubleInv to inv1::input;
  connect inv1.output to inv2::input;
  connect inv2.output to outputDoubleInv::output;
}
