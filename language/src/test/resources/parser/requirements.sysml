requirement def <"Human ID"> R1 {
  subject s:S;
}

satisfy requirement r1:R1 {
  subject s:S;
}

satisfy requirement r1_1:R1 {
  constraint vanilla;
  assume constraint assumptions;
  require shortcut;
}
