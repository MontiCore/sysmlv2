send reference.to.something to somewhere.elsewhere;

assign reference.to.someting := some + complex * expression;

accept handle:Type via some.reference;

accept handle;

action def Jump;

action def Around {
  action jump: Jump;
  action jumpjumpjump;
}

action {
  send a to b;
  assign x := y;
  fork a;
    then c;
    then d;

  decide a;
    if true then c;
    if false then d;

  join joinNode;

  action c;
  merge e;

  perform a;
}
