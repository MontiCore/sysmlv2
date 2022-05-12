package automata {
  state def AutomatonInverter(in i: Boolean, out o: Boolean){
    entry;
      then S;

    state S;

    transition invert
      first S
      accept i
      if true
      do send !i to o
      then S;
  }
}
