part def inverterSpec {
  port input : bool_port;
  port output : ~bool_port;

  satisfy requirement outputVerification {
    require constraint correctInversion {
      !input.in_dinv == output.in_dinv
    }
  }
}
