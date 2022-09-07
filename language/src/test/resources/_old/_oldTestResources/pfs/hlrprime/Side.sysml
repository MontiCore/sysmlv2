package hlrprime {
  import verification::DefaultTypes::Pair;

  port def InPair {
    in value val: Pair<Boolean, Boolean>;
  }

  port def InBool {
    in value val: Boolean;
  }

  part def Side(initialSide: Boolean) {
  	port clk: InBool;
  	port ts: InBool;
    port ospf: InPair;
    port pf: ~InPair;

    // TODO #Issue2 Sowas parst, aber sieht dann im AST natürlich ganz anders aus. Irgendwas cleveres machen, dass es intern
    //      gleich behandelt werden oder verbieten? Noch besser mit "port" statt value! Aber "port" erlaubt kein "in".
    //in value pf: Pair<Boolean, Boolean>;
    //port pf: Pair<Boolean, Boolean>;

    assert constraint {
      // TODO #Issue3 "FieldAccess" in CommonExpressions (monticore) verlangt "." statt "::" (ohne doppelte CallExpression klappt das auch mit "::")
      initialSide implies (pf.nth(0).getFirst() && pf.nth(0).getSecond())
    }

  }

  // TODO #issue5 Alle Constraints übertragen
  /*requirement {
    subject side: Side;

    // TODO #Issue4 Solche Variablen wären schön - wird allerdings auch eine Herausforderung das zu transformieren. Und muss in
    //         constraints (statt requirements) funktionieren
    value in = side::ospf::nth;
    value out = side::pf::nth;

    // 1
    require constraint {
      side::initialSide implies (out(0)::getFirst() && out(0)::getSecond());
    }

    // 2
    require constraint {
     !side::initialSide implies (!out(0)::getFirst() && out(0)::getSecond());
    }

    // 3
    require constraint {
      nat -> forAll n (
        (out(n)::getFirst() && out(n)::getSecond() && clk::nth(n) && in(n)::getFirst()
        && !in(n)::getSecond())
        implies (!out(n+1)::getFirst() && !out(n+1)::getSecond())
      )
    }

    // 4
    require constraint {
      nat -> forAll n (
        (out(n)::getFirst() && out(n)::getSecond() && !(clk::nth(n) && in(n)::getFirst()
        && !in(n)::getSecond()))
        implies (out(n+1)::getFirst() && out(n+1)::getSecond())
      )
    }

    // 5
    require constraint {
      nat -> forAll n (
        (!out(n)::getFirst() && !out(n)::getSecond() && clk::nth(n) && in(n)::getFirst()
        && in(n)::getSecond())
        implies (!out(n+1)::getFirst() && out(n+1)::getSecond())
      )
    }

    // 6
    require constraint {
      nat -> forAll n (
        (!out(n)::getFirst() && !out(n)::getSecond() && !(clk::nth(n) && in(n)::getFirst()
        && in(n)::getSecond()))
        implies (!out(n+1)::getFirst() && !out(n+1)::getSecond())
      )
    }

    // 7
    require constraint {
      nat -> forAll n (
        (!out(n)::getFirst() && out(n)::getSecond() && clk::nth(n) && ts::nth(n) && !(if (n>0) then ts::nth(n-1); else true;))
        implies (out(n+1)::getFirst() && !out(n+1)::getSecond())
      )
    }

    // 8
    require constraint {
      nat -> forAll n (
        (!out(n)::getFirst() && out(n)::getSecond() && !(clk::nth(n) && ts::nth(n) && !(if (n>0) then ts::nth(n-1); else true;)))
        implies (!out(n+1)::getFirst() && out(n+1)::getSecond())
      )
    }

    // 9
    require constraint {
      nat -> forAll n (
        (out(n)::getFirst() && !out(n)::getSecond() && clk::nth(n) && !in(n)::getFirst()
        && !in(n)::getSecond())
        implies (out(n+1)::getFirst() && out(n+1)::getSecond())
      )
    }

    // 10
    require constraint {
      nat -> forAll n (
        (out(n)::getFirst() && !out(n)::getSecond() && !(clk::nth(n) && !in(n)::getFirst()
        && !in(n)::getSecond()))
        implies (out(n+1)::getFirst() && !out(n+1)::getSecond())
      )
    }
  }*/

}
