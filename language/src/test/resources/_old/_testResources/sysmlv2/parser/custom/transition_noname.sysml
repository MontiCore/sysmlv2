// Page 212 in https://github.com/Systems-Modeling/SysML-v2-Release/blob/master/doc/2-OMG_Systems_Modeling_Language.pdf
state S {
  entry; then state1;

  state state1;

  transition
    first state1
    then state2;

  state state2;
}
