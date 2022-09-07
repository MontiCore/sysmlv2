package code {
  import verification::DefaultTypes::Pair;

  state def EventBusAutomaton(
    in clk: Boolean,
    in i: Pair<Boolean, Boolean>,
    out o: Pair<Boolean, Boolean>,
    initialSide: Boolean)
  {
    /* Meherere "sends" abhängig von einem Guard sind nicht möglich */
    entry
      action { send (Pair<Boolean, Boolean>(initialSide, false)) to lastVal;}
      if initialSide
      then Other;
      if !initialSide
      then Single;

    value lastVal: Pair<Boolean, Boolean>;

    state Single;

    transition
      first Single
      accept Tick
      do action { send i to o; lastVal = i; }
      then Single;

    transition
      first Single
      accept clk
      if clk == false
      do action { send lastVal to o; }
      then Single;
  }

  part def EventBus(initialSide: Boolean) {
    port clk: InBool;
    port i: InPair;
    port o: ~InPair;

    state behavior: EventBusAutomaton(clk = clk::val, i = i::val, o = o::val, initialSide);
  }

}
