part def Inverter_LLR1 {
  port input: Booleans;
  port output: ~Booleans;

  exhibit state behavior {
    entry;
      then S;

    state S;

    transition
      first S
      accept input.val
      if input.val == true
      do  action { send false to output.val; }
      then S;
  }
}