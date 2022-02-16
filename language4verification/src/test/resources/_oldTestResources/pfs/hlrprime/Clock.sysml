package hlrprime {

  port def OutBool {
    out value val: Boolean;
  }

  part def Clock {
    port clk: OutBool;

    assert constraint {
      clk::length() == INF
    }

    assert constraint {
      clk::length() != -INF
    }
  }

}

