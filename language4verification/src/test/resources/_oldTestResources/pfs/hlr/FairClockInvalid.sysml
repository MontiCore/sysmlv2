package hlr {
  import hlrprime::Clock;

  port def OutBool {
    out value val: Boolean;
  }

  part def FairClock refines UnfairClock {
    port clk: OutBool;

    assert constraint {
      clk::length() == INF
    }

    assert constraint {
      forall nat n:
        exists nat m:
          n<m && m<n+10 && fairClock::clk::nth(m)
    }

  }
}
