part def doubleInverterSpec {
  port inputDoubleInv : bool_port;
  port outputDoubleInv : ~bool_port;

  satisfy requirement outputVerification {
    require constraint InputEqOutput {
      inputDoubleInv.in_dinv == outputDoubleInv.in_dinv
    }
  }
}
