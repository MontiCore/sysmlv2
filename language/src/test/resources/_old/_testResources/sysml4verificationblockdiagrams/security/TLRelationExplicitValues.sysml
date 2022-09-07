/**
 * Beispiele für korrekt angegebene TrustLevelRelations
 */
package easyVerification {

  part def SecurePart {

    // TrustLevel for the SecurePart part def
    trustLevel +1 "physical lock";

    trustLevelRelation riskyPart < securePart;

    // part usages of parts with a trust level
    part riskyPart : SecurityRiskyPart;
    part securePart : EvenSecurerPart;

  }

  part def SecurityRiskyPart {
    trustLevel -10 "no password";
  }

  part def EvenSecurerPart {
    trustLevel +2 "trust me, its a very good and secure part - in fact its the most secure part ever";
  }

}
