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

    // TODO Hier könnten der Vollständigkeit halber alle Reqs aus Rhapsody stehen

    requirement def <"HLR.1"> PriorityHLR :> Priority {
      subject scheduler: PriorityScheduler;

      assume constraint { scheduler.linkCapacity > 2 * behavior.user1.maximumPackageSize() }
      require constraint {
        scheduler.buffer1.beforeInterval(t).length() > 0
            implies scheduler.decision.inInterval(t).filterLengthGreater(3).length() > 0
      }
    }

    requirement def <"HLR.2"> StarvationHLR :> Starvation {
      subject scheduler: PriorityScheduler;

      requirement p1_not_starving {
        assume constraint { scheduler.user1.length() == INF }
        require constraint { scheduler.decision.filterLengthGreater(3).length() == INF }
      }

      requirement p2_not_starving {
        assume constraint { scheduler.user2.length() == INF }
        require constraint { scheduler.decision.filterLengthEqual(3).length() == INF }
      }

      requirement p3_not_starving {
        assume constraint { scheduler.user3.length() == INF }
        require constraint { scheduler.decision.filterLengthEqual(2).length() == INF }
      }

      requirement p4_not_starving {
        assume constraint { scheduler.user4.length() == INF }
        require constraint { scheduler.decision.filterLengthEqual(1).length() == INF }
      }
    }
  }

  package 'Architecture Modeling' {
    attribute def Packet {
      value size: Integer;
    }

    // TODO Oder direkt "Buffer" durch "List<Packet>" ersetzen bei allen Vorkommen!
    attribute def Buffer {
      // TODO Funktionen head und tail modellieren oder aus "lib" nehmen für Trafo
      // TODO tail: self => Packet
      // TODO tail: self => Buffer
    }

    port def Packets {
      in data: Packet;
    }

    part def User {
      port output: ~Packets;
    }

    part def IO_4x1 {
      port input1: Packets;
      port input2: Packets;
      port input3: Packets;
      port input4: Packets;
      port output: ~Packets;
    }

    part def IO_1x1 {
      port input1: Packets;
      port output: ~Packets;
    }

    part def MessageRouter {
      port input: Packets;
      port priority1: ~Packets;
      port priority2: ~Packets;
      port priority3: ~Packets;
      port priority4: ~Packets;
    }

    part def PriorityScheduler {
      port priority1: Packets;
      port priority2: Packets;
      port priority3: Packets;
      port priority4: Packets;
      port decision: ~Packets;

      value buffer1: Buffer;
      value buffer2: Buffer;
      value buffer3: Buffer;
      value buffer4: Buffer;

      value ready1: Boolean = False;
      value ready2: Boolean = False;
      value ready3: Boolean = False;
      value ready4: Boolean = False;

      value linkCapacity = 50; // TODO Könnte Parameter werden, aber eigentlich wollen wir nicht drüber reden
      value remainingCapacity = linkCapacity;

      exhibit state behavior: PriorityScheduling(
          in priority1 = priority1.data,
          in priority2 = priority2.data,
          in priority3 = priority3.data,
          in priority4 = priority4.data,
          out decision = decision.data
      ) {
        entry;
        then Working;

        /*
         * User-Input, append to buffer
         * TOTAL: 4
         */

        transition user_input
          accept priority1
          do assign buffer1 := buffer1.append(priority1)
          then Working;

        // ... (3 more)

        /*
         * Time, concat all buffers and take the max. Then clear the sent data from buffers.
         * TOTAL: 1
         */

        // TODO @HKa Ich brauche Epsilon-Transitionen!!
        transition start_making_decisions
          accept Time
          do action {
            assign ready1 := True; // Starts Epsilon-Transitions
            assign ready2 := True;
            assign ready3 := True;
            assign ready4 := True;
          }
          then Working;

        /*
         * Nominale Selektion einer Priority: Event "ready" für diese Prio und keine höhere ist Bereit. Buffer gefüllt,
         * Kapazität ausreichen.
         * TOTAL: 4
         */

        transition nominal_selection_1
          accept when ready1
          if buffer1.length() != 0 && buffer1.head().length() <= remainingCapacity
          do action {
            send buffer1.head() to decision;
            assign buffer1 := buffer1.tail();
            assign remainingCapacity := remainingCapacity - buffer1.head().length(); // Reihenfolge spielt keine Rolle!
            assign ready1 := False;
          }
          then Working;

        // ... (2 more)

        transition nominal_selection_4
          accept when ready4
          if !ready3 && !ready2 && !ready1 && buffer4.length() != 0 && buffer4.head().length() <= remainingCapacity
          do action {
            send buffer4.head() to decision;
            assign buffer4 := buffer4.tail();
            assign remainingCapacity := remainingCapacity - buffer4.head().length(); // Reihenfolge spielt keine Rolle!
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := True;
            assign ready4 := False;
          }
          then Working;

        /*
         * Reset wenn alle False werden. Prio 1 wird dabei als letztes False, deswegen "!ready1" als "Trigger".
         * TOTAL: 1
         */

        transition reset
          accept when !ready1 // TODO Alternativ: "ready5"
          if !ready4 && !ready3 && !ready2
          do action {
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := True;
            assign ready4 := True;
          }
          then Working;

        /*
         * Eigentliche Priorität (höchstes "ready") kann aus verschiedenen Gründen nicht. Wir füllen ohne ready-Klappen
         * einfach mit höchster Priorität nach! Jeweils nach Prio X kann nicht, Prio Y wird geprüft, für alle Y != X.
         * TOTAL: 12
         */

        transition fallback_from_1_to_2
          accept when ready1
          if (buffer1.length() == 0 || buffer1.head().length() > remainingCapacity) && buffer2.length() > 0
              && buffer2.head().length() <= remainingCapacity
          do action {
            send buffer2.head() to decision;
            assign buffer2 := buffer2.tail();
            assign remainingCapacity := remainingCapacity - buffer2.head().length();
            // TODO Problem, ab jetzt keinen Trigger mehr... doch Klappen bewegen, andere Variablen einführen,...?
            // TODO Lösung vielleicht DLUFv3: "Reste" konkatinieren, abschneiden (Achtung: Länge einzelner Pakete -.-')
          }
          then Working;

        transition fallback_from_1_to_3
          accept when ready1
          if (buffer1.length() == 0 || buffer1.head().length() > remainingCapacity) && (buffer2.length() == 0
              || buffer2.head().length() > remainingCapacity) && buffer3.length() > 0
              && buffer3.head().length() <= remainingCapacity
          do action {
            send buffer3.head() to decision;
            assign buffer3 := buffer3.tail();
            assign remainingCapacity := remainingCapacity - buffer3.head().length();
            // TODO s.o.
          }
          then Working;

        // (...1 more)

        transition fallback_from_2_to_1
          accept when ready2
          if !ready1 && (buffer2.length() == 0 || buffer2.head().length() > remainingCapacity) && buffer1.length() > 0
              && buffer1.head().length() <= remainingCapacity
          do action {
            send buffer1.head() to decision;
            assign buffer1 := buffer1.tail();
            assign remainingCapacity := remainingCapacity - buffer1.head().length();
            // TODO s.o.
          }
          then Working;

        // (...2 more)

        transition fallback_from_3_to_1
          accept when ready3
          if !ready1 && !ready2 && (buffer3.length() == 0 || buffer3.head().length() > remainingCapacity)
              && buffer1.length() > 0 && buffer1.head().length() <= remainingCapacity
          do action {
            send buffer1.head() to decision;
            assign buffer1 := buffer1.tail();
            assign remainingCapacity := remainingCapacity - buffer1.head().length();
            // TODO s.o.
          }
          then Working;

        // (...2 more)

        transition fallback_from_4_to_1
          accept when ready4
          if !ready1 && !ready2 && !ready3 && (buffer4.length() == 0 || buffer4.head().length() > remainingCapacity)
              && buffer1.length() > 0 && buffer1.head().length() <= remainingCapacity
          do action {
            send buffer1.head() to decision;
            assign buffer1 := buffer1.tail();
            assign remainingCapacity := remainingCapacity - buffer1.head().length();
            // TODO s.o.
          }
          then Working;

        // (...2 more)
      }
    }

    part def DataLink {
      port input: DataSink;
    }

    part def Upload {
      port user1: Packets;
      port user2: Packets;
      port user3: Packets;
      port user4: Packets;
      port output: ~Packets;

      part io_in: IO_4x1;
      part router: MessageRouter;
      part scheduler: PriorityScheduler;
      part io_out: IO_1x1;

      /* TODO Kaputt??
      connect user1 to io_in.input1;
      connect user2 to io_in.input2;
      connect user3 to io_in.input3;
      connect user4 to io_in.input4;
      connect io_in.output to router.input;
      connect router.priority1 to scheduler.priority1;
      connect router.priority2 to scheduler.priority2;
      connect router.priority3 to scheduler.priority3;
      connect router.priority4 to scheduler.priority4;
      connect scheduler.decision to io_out.in;
      connect io_out.output to output;
      */

      satisfy hlr.Priority by scheduler;
    }

    part context {
      part user1: User;
      part user2: User;
      part user3: User;
      part user4: User;
      part upload: Upload;
      part link: Datalink;

      connect user1.output to upload.user1;
      connect user2.output to upload.user2;
      connect user3.output to upload.user3;
      connect user4.output to upload.user4;
      connect upload.output to link.input;
    }
  }
}
