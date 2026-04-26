requirement Repro0xF737F {
  subject c : SomePart;
  require constraint {
    c.behavior.something == true
  }
}

part def SomePart {
  #delayed exhibit state behavior {
    state S;
  }
}
