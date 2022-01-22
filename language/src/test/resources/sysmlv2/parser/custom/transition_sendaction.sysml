state def Automaton {

  state S;

  transition single_sendusage
    first S
    do send some.val + 1 to myport
    then T;

  transition nested_sendusage
    first S
    do action { send x to y; send s to t; }
    then T;

  state T;

}
