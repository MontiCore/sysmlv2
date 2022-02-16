package hlrprime {
  import verification::DefaultTypes::Pair;

  port def OutPair {
    out value val: Pair<Boolean, Boolean>;
  }

  part def Redundancy {
    port clk1: InBool;
    port clk2: InBool;
    port clk3: InBool;
    port clk4: InBool;
    port ts: InBool;
    port lpf: OutPair;
    port rpf: OutPair;

    part lside: Side(initialSide = true);
    part lrbus: Bus(initialSide = true);
    part rside: Side(initialSide = false);
    part rlbus: Bus(initialSide = false);

    connect clk1 to lside::clk;
    connect clk2 to lside::clk;
    connect clk3 to lside::clk;
    connect clk4 to lside::clk;
    connect ts to lside::ts;
    connect ts to rside::ts;
    connect lside::pf to lrbus::i;
    connect lside::pf to lpf;
    connect lrbus::o to rside::ospf;
    connect rside::pf to rlbus::i;
    connect rside::pf to rpf;
    connect rlbus::o to lside::ospf;
  }

}
