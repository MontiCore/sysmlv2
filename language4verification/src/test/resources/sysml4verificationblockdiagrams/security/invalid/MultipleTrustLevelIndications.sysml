/**
 * Beispiel für mehrmalige Angabe eines TrustLevels pro part
 */
package invalidTL {

  part def InvalidPart {
    trustLevel +1 "password lock";

    trustLevel +10 "physical lock";
  }
}
