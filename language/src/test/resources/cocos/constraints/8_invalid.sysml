part def LogicBasedConstraints {

  constraint streams {
    forall i in {1,2,3,4}:
      i < 10
  }
}
