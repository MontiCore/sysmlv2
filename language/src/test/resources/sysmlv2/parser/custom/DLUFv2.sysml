/**
 * [CONFIDENTIAL] Handle with care
 */
package 'Data Link Upload Feed - A Case Study' {

  port def DecisionInputs {
    /* Buffer ist Teil des Zustands, inkl. der Daten im Buffer */
    in user1: Package;
    in user2: Package;
    in user3: Package;
    in user4: Package;
  }

  port def DecisionOutputs {
    // TODO Als Liste modellieren, oder einem Event-Automaten erlauben Listen auszugeben?
    out link: Package;
  }

  part decision {
    port i: DecisionInputs;
    port o: DecisionOutputs;

    value buffer1: Buffer;
    value buffer2: Buffer;
    value buffer3: Buffer;
    value buffer4: Buffer;

    // TODO Eigentlich soll linkCap während Laufzeit ändern, muss also Teil von I/O (ports) sein!
    exhibit state(in linkCapacity: nat) {
      entry;
      then Working;

      /*
       * User-Input, append to buffer
       * TOTAL: 4
       */

      transition user_input
        accept user1
        do send append(user1) to buffer1 // TODO "Methode" ist Senden eines Signals... Actions erlauben scheinbar kein "a.b();"
        then Working; // TODO herausfinden wieviel man bei Transitionen weglassen kann

      // ... (3 more)

      /*
       * Time, concat all buffers and take the max. Then clear the sent data from buffers.
       * TOTAL: 1
       */

      transition decision_making
        accept Time
        do action {
          send buffer1.append(buffer2).append(buffer3).append(buffer4) / linkCapacity to link;
          send buffer1 % linkCapacity to buffer1;
          send buffer2 % (linkCapacity - buffer1.count()) to buffer2;
          send buffer3 % (linkCapacity - buffer1.count() - buffer2.count()) to buffer3;
          send buffer3 % (linkCapacity - buffer1.count() - buffer2.count() - buffer3.count()) to buffer4;
        }
        then Working;
    }
  }

  requirement def <"R1"> Priority  {
    subject behavior = decision;

    assume constraint { /* TODO ??? */ }
    require constraint { /* TODO ??? */ }
  }
}
