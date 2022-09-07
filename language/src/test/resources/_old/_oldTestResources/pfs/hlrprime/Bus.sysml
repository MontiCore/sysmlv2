package hlrprime {
  import verification::DefaultTypes::Pair;

  part def Bus(initialSide: Boolean) {
  	port clk: InBool;
  	port i: InPair;
    port o: ~InPair;

    value lastVal: Pair<Boolean, Boolean>;

    /* fun pred1 :: "bool ⇒ bool stream × (bool×bool) stream ⇒ (bool×bool) stream ⇒ bool" where
     * "pred1 initialSide (clock, i) out = (0<#(clock) ∧ shd (clock) = False ⟶ (shd out)=(initialSide,True))"
     */
    assert constraint {
      ((clk::length() > 0 && i::length() > 0) && clk::nth(0) == false)
      implies
      (o::nth(0).getFirst()==initialSide && o::nth(0).getSecond())
    }

    /* fun pred2 :: "bool ⇒ bool stream × (bool×bool) stream ⇒ (bool×bool) stream ⇒ bool" where
     * "pred2 initialSide (clock, i) out = (∀ n. (Fin n<#clock ∧ (snth n clock)= True) ⟶ ((snth n out) = snth n i))"
     */
    assert constraint {
      nat -> forAll n (
        ((n < clk::length() && n<i::length()) && clk::nth(n)) implies (i::nth(n) == o::nth(n))
      )
    }

    /* fun pred3 :: "bool ⇒ bool stream × (bool×bool) stream ⇒ (bool×bool) stream ⇒ bool"where
     * "pred3 initialSide (clock, i) out = (∀ n. (Fin (Suc n)<#clock ∧(snth (Suc n) clock)= False) ⟶ ((snth (Suc n) out) = snth n out))"
     */
    assert constraint {
      nat -> forAll n (
        ((n+1 < clk::length() && n+1 < i::length()) && !clk::nth(n+1)) implies (o::nth(n) == o::nth(n+1))
      )
    }
  }

}
