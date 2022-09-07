/**
 * Beispiele für korrekt angegebenes Timing (kein Timing ist erlaubt, Default sollte INSTANT sein
 */
package valid {

  part def DelayedPart {
    timing delayed;
  }

  part def InstantPart {
    timing instant;
  }

  part def NoTiming { }

  part def NoTimingAndNoBody;

}
