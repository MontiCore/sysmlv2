state def Initial;

state def Machine {
  entry;
  then I;

  state I;

  transition
    first I
    accept someEvent
    if condition
    do action someAction
    then O;

}

exhibit state Behavior {
  entry;
  then S;
}
