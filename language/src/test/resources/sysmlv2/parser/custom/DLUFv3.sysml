/**
 * [CONFIDENTIAL] Handle with care
 */
package 'Data Link Upload Feed - A Case Study' {

  package 'Requirement Modeling' {

    requirement def <"SysReq.1"> Priority {
      doc /* Nachrichten eines höher-priorisierten Kanals werden bevorzugt bearbeitet */
    }

    requirement def <"SysReq.2"> Starvation {
      doc /* Keiner der Eingabekanäle darf unter gewöhnlichen Bedingungen komplett ignoriert werden */
    }
  }

  package 'Architecture Modeling' {
    import 'Behavior Specification'::DecisionStateMachine;

    attribute def Packet;

    port def DataSource {
      in data: Packet;
    }

    port def DataSink {
      out data: Packet;
    }

    part def User {
      port output: DataSource;
    }

    part context {

      part user1: User;
      part user2: User;
      part user3: User;
      part user4: User;
      part decision: Decision;

      connect user1.output to decision.user1;
      connect user2.output to decision.user2;
      connect user3.output to decision.user3;
      connect user4.output to decision.user4;

      satisfy hlr.Priority by decision;
    }
  }

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

  part def Decision {
    port i: DecisionInputs;
    port o: DecisionOutputs;

    value buffer1: Buffer;
    value buffer2: Buffer;
    value buffer3: Buffer;
    value buffer4: Buffer;

    value linkCapacity = 50;

    exhibit state {
      entry;
      then Working;

      /*
       * User-Input, append to buffer
       * TOTAL: 4
       */

      // TODO herausfinden wieviel man bei Transitionen weglassen kann
      transition user_input
        accept i.user1
        do assign buffer1 := buffer1.append(i.user1)
        then Working;

      // ... (3 more)

      /*
       * Time, concat all buffers and take the max. Then clear the sent data from buffers.
       * TOTAL: 1
       */

      transition decision_making
        accept Time
        do action {
          send buffer1.append(buffer2).append(buffer3).append(buffer4) / linkCapacity to o.link;
          send buffer1 % linkCapacity to buffer1;
          send buffer2 % (linkCapacity - buffer1.count()) to buffer2;
          send buffer3 % (linkCapacity - buffer1.count() - buffer2.count()) to buffer3;
          send buffer3 % (linkCapacity - buffer1.count() - buffer2.count() - buffer3.count()) to buffer4;
        }
        then Working;
    }
  }

  requirement def <"R1"> Latency {
    subject behavior: Decision;

    assume constraint { /* TODO ??? */ }
    require constraint { /* TODO ??? */ }
  }
}
