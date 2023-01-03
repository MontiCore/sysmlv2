/* (c) https://github.com/MontiCore/monticore */
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
exhibit aCertainState;
state Behavior {
  entry;
  then S;
}

state operationalStates {
  entry;
  do action { send a to b; }
  exit someAction;
  then off;
}

