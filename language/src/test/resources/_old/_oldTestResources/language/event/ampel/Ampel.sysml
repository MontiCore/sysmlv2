package ampel {

  /**
   * Simuliert eine Ampel in SysML4Verification 2.0 (nicht veröffentlicht)
   * Im Gegensatz zu dem MA-Modell etwas wenig Syntactic Sugar.
   * Testweise mit Strings.
   * Siehe https://git.rwth-aachen.de/montibelle/frontend/language/montiarcverification/-/blob/project6/src/test/resources/eventautomata/Ampel.arc
   */
  state def AmpelAutomaton(
    in red: Boolean,
    in orange: Boolean,
    in green: Boolean,
    out color: String)
  {
    entry action {
     send "green" to color; send 0 to counter;
    }

    then Green;

    /* entry action {
     *   send "red" to color; send 0 to counter;
     * }
     *
     * then Red;
     */

    value counter: Natural;
    state Green;
    state Orange;
    state Red;

    // Beispielhaft eine "Tick"-Transition...
    transition
      first Green
      accept Tick
      if counter==0
      do action { send "green" to color; send 1 to counter; }
      then Green;

    // ...und zwei "Event"-Transitionen!
    transition
      first Green
      accept green
      do action { send "" to color; send 1 to counter; } /* output muss gesetzt sein */
      then Green;

    transition
      first Green
      accept orange
      do action { send 0 to counter; } /* output muss gesetzt sein */
      then Orange;

    // etc.
  }

  port def BooleanInputPort {
    in val: Boolean;
  }

  port def ColorOutputPort {
    out val: String;
  }

  part def Ampel {
    port red: BooleanInputPort;
    port orange: BooleanInputPort;
    port green: BooleanInputPort;
    port color: ColorOutputPort;

    state behavior: AmpelAutomaton(red = red::val, orange = orange::val, green = green::val, color = color::val);
  }

}
