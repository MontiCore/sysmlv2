requirement Repro0xFD226 {
  attribute input : boolean;
  part def S {
    attribute input : int;
  }
  subject s : S;
  require constraint {
    input = true
  }
}
