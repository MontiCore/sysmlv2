/**
 * Beispiele für korrekt angegebenes TrustLevel
 */
package valid {

  part def SecurePart {
    trustLevel +1 "physical lock";
  }

  part def SecurityRiskyPart {
    trustLevel -10 "no password";
  }
}
