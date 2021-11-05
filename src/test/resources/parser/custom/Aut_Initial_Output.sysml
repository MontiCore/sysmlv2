state def Automaton(in i: Integer, out o: Integer) {
  entry
    send 4 to o;
    then S;

  state S;

  transition plusFive
    first S
    do send (i+5) to o
    then S;
}
