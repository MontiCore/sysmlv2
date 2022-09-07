package code {
  import verification::DefaultTypes::Pair;

  state def BusAutomaton(
    in clk: Boolean,
    in i: Pair<Boolean, Boolean>,
    out o: Pair<Boolean, Boolean>,
    initialSide: Boolean)
  {
    // Hier mal die Alternative initiale Belegung (vgl. InvalidDelayedClock)
    entry
      send (Pair<Boolean, Boolean>(initialSide, true)) to lastVal;
      then Single;

    value lastVal: Pair<Boolean, Boolean>;

    state Single;

    transition
      first Single
      if clk == true
      do action { send i to o; lastVal = i; }
      then Single;

    transition
      first Single
      if clk == false
      do action { send lastVal to o; }
      then Single;
  }

  port def InBool {
    in val: Boolean;
  }

  port def InPair {
    in val: Pair<Boolean, Boolean>;
  }

  part def Bus(initialSide: Boolean) {
    port clk: InBool;
    port i: InPair;
    port o: ~InPair;

    state behavior: BusAutomaton(clk = clk::val, i = i::val, o = o::val, initialSide);
  }

}
