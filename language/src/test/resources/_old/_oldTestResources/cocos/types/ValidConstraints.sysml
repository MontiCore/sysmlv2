package valid {

  constraint def QuickMaths {
    2 + 2 == 4
  }

  constraint def SlowMaths {
    10 < INF
  }

  constraint def ParaMaths(x: Integer) {
    x == 3
  }

  constraint def OCLMaths(xs: List<Integer>) {
    forall x in xs: x == 0
  }

  constraint def AdditionEquation(x: Integer, y: Integer, z: Integer) {
    x + y == z
  }

  part def GoodPart {
    constraint checkParaMaths : ParaMaths(x = 3);
    assert constraint checkParaMaths : ParaMaths(x = 3);

    // TODO: Wie funktionieren ad-hoc Listen?
    //constraint checkOCLMaths : OCLMaths(xs = {1,2,3});

    constraint checkSlowMaths : SlowMaths();
    assert constraint checkSlowMaths : SlowMaths();

    constraint equation : AdditionEquation(x = 1, y = 1, z = 2);
    assert constraint equation : AdditionEquation(x = 1, y = 1, z = 2);
  }

}
