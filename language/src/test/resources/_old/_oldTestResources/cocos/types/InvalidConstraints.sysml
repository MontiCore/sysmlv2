package invalid {

  constraint def NoMaths {
    10 < true
  }

  constraint def StrMaths {
    "two plus two is four"
  }

  constraint def ParaMaths(x: int) {
    x - 1
  }

  constraint def OCLMaths(xs: List<Integer>) {
    forall x in xs: x + 1
  }

  constraint def BadType(x: Blub) {
    x - 1
  }

  constraint def AdditionEquation(x: Integer, y: Integer, z: Integer) {
    x + y == z
  }

  part def BadPartAsserts {
    assert constraint checkNonExistent : NonExistent();
  }

  part def BadPartDefNonExistent {
    constraint checkNonExistent : NonExistent();

    assert constraint checkNonExistent : NonExistent();
  }

  part def PartTooManyArguments {
    // Hier wird die "ad-hoc Liste" {1,2,3} gar nicht erst ausgewertet, weil die Anzahl der Parameter nicht stimmt
    constraint checkOCLMaths : OCLMaths(xs = {1,2,3}, a = 1);

    assert constraint checkParaMaths : ParaMaths(x = true - 1, y = 1);
  }

  part def PartTooFewArguments {
    constraint checkOCLMaths : OCLMaths();

    assert constraint checkParaMaths : ParaMaths();
  }

  part def PartUnderivableType {
   constraint checkOCLMaths : OCLMaths(a = 1 < true);

   assert constraint checkOCLMaths : OCLMaths(a = 1 < true);
  }

  part def PartWrongType {
    constraint checkParaMaths : ParaMaths(x = "3");

    assert constraint checkParaMaths : ParaMaths(x = "3");
  }

}
