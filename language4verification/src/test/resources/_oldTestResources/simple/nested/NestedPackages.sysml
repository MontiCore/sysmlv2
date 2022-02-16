package Package_Top {
  package Package_A {
    part def A;
  }
  package Package_B {
    package Package_BA {
      part def BA;
    }
    part def B;
  }
  part def Top;
}

package Package_Top2 {
  part def Top2;

  port def Top_Port {
    in val: Boolean;
  }
}
