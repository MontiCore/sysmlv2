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

      assume constraint { scheduler.linkCapacity > 2 * MAXIMUM_PACKAGE_SIZE }
      require constraint {
        // TODO HKa
      }
    }

    requirement def <"HLR.2"> StarvationHLR :> Starvation {
      subject scheduler: PriorityScheduler;

      requirement p1_not_starving {
        assume constraint { scheduler.priority1.length() == INF }
        require constraint { scheduler.decision.filterLengthGreater(3).length() == INF }
      }

      requirement p2_not_starving {
        assume constraint { scheduler.priority2.length() == INF }
        require constraint { scheduler.decision.filterLengthEqual(3).length() == INF }
      }

      requirement p3_not_starving {
        assume constraint { scheduler.priority3.length() == INF }
        require constraint { scheduler.decision.filterLengthEqual(2).length() == INF }
      }

      requirement p4_not_starving {
        assume constraint { scheduler.priority4.length() == INF }
        require constraint { scheduler.decision.filterLengthEqual(1).length() == INF }
      }
    }
  }

  package 'Architecture Modeling' {
    attribute def Packet {
      value size: Integer;
    }

    port def Packets {
      in data: Packet;
    }

    port def Booleans {
      in data: Boolean;
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

    part def Buffer {
      port input: Packets;
      port repeat: Booleans;
      port output: Packets;
      port empty: ~Booleans;

      value queue: List<Packet>;

      exhibit state behavior: Buffering (
          in input = input.data,
          in repeat = repeat.data,
          out output = output.data
      ) {
        entry;
        then Inhibited;

        // Incoming messages will be held back until a "repeat" indicates otherwise
        state Inhibited;

        transition new_packet
          first Inhibited
          accept input
          do action {
            assign queue := queue.append(input);
          }
          then Inhibited;

        transition last_available
          first Inhibited
          accept repeat
          if repeat && queue.length() > 0
          do send queue.head() to output
          then Inhibited;

        /** TODO Kann nicht vorkommen
        transition last_empty
          first Inhibited
          accept repeat
          if repeat && queue.length() == 0
          then Forwarding;
        */

        transition next_available
          first Inhibited
          accept repeat
          if !repeat && queue.length() >= 2
          do action {
            send queue.tail().head() to output;
            assign queue := queue.tail()
          }
          then Inhibited;

        transition next_not_available
          first Inhibited
          accept repeat
          if !repeat && queue.length() == 1
          do action {
            assign queue := queue.tail(); // empty list
            send True to empty;
          }
          then Forwarding;

        /** TODO Kann nicht vorkommen
        transition next_empty
          first Inhibited
          accept repeat
          if !repeat && queue.length() == 0
          do action {
            send True to empty; // empty = False wird weder gesendet noch beachtet, deckt sich mit Sendung v. Paketen
          }
          then Forwarding;
        */

        // Incoming messages will be forwarded immediately
        state Forwarding;

        transition forward_new_packet
          first Forwarding
          accept input
          if queue.length() = 0 // TODO Eigentlich unnötig zu prüfen
          do action {
            assign queue := queue.append(input);
            send input to output;
          }
          then Inhibited;

        /** TODO Kann nicht vorkommen
        transition forward_old_packet
          first Forwarding
          accept input
          if queue.length() > 0
          do action {
            assign queue := queue.append(input);
            send queue.head() to output;
          }
          then Inhibited;
        */

        /** TODO Kann nicht vorkommen
        transition repeat_while_forwarding
          first Forwarding
          accept repeat
          if repeat ... TODO Alle Kombinationen aus "repeat" und "queue.length 0 / 1 / 2+"
        */
      }
    }

    part def PriorityScheduler {
      port priority1: Packets;
      port priority2: Packets;
      port priority3: Packets;
      port priority4: Packets;

      /* Ob noch Nachrichten da sind (nur True ist relevant, False deckt sich mit Ankunft von Paketen) */
      port isEmpty1: Booleans;
      port isEmpty2: Booleans;
      port isEmpty3: Booleans;
      port isEmpty4: Booleans;

      /* Wdh. oder nächstes */
      port repeat1: ~Booleans;
      port repeat2: ~Booleans;
      port repeat3: ~Booleans;
      port repeat4: ~Booleans;

      port decision: ~Packets;

      value ready1: Boolean = False;
      value ready2: Boolean = False;
      value ready3: Boolean = False;
      value ready4: Boolean = False;

      /* Ob für dieses Intervall die Nachricht nicht mehr ins Rest-Budget passt */
      value exceeded1: Boolean = False;
      value exceeded2: Boolean = False;
      value exceeded3: Boolean = False;
      value exceeded4: Boolean = False;

      /* Ob Buffer ein empty=True gemeldet hat (False nicht beachtet, deckt sich mit Ankunft von Paket) */
      value empty1: Boolean = False;
      value empty2: Boolean = False;
      value empty3: Boolean = False;
      value empty4: Boolean = False;

      value linkCapacity = 50; // TODO Könnte Parameter werden, aber eigentlich wollen wir nicht drüber reden
      value remainingCapacity = linkCapacity;

      exhibit state behavior: PriorityScheduling(
          in priority1 = priority1.data,
          in priority2 = priority2.data,
          in priority3 = priority3.data,
          in priority4 = priority4.data,
          out isEmpty1 = isEmpty1.data,
          out isEmpty2 = isEmpty2data,
          out isEmpty3 = isEmpty3.data,
          out isEmpty4 = isEmpty4.data,
          out repeat1 = repeat1.data,
          out repeat2 = repeat2.data,
          out repeat3 = repeat3.data,
          out repeat4 = repeat4.data,
          out decision = decision.data
      ) {
        entry;
        do action {
          send False to repeat1; // Das "Selbe" (dh. Erste) nochmal
          send False to repeat2;
          send False to repeat3;
          send False to repeat4;
        }
        then Working;

        /*
         * Nominale Selektion der höchsten ready- und nicht-empty-Priority. Die "remainingCapacity" ist ausreichend.
         * TOTAL: 4
         */

        transition nominal_selection_1
          accept priority1
          if ready1 && priority1.length() <= remainingCapacity
          do action {
            send priority1 to decision;
            send False to repeat1;
            assign remainingCapacity := remainingCapacity - priority1.length();
            assign ready1 := False;
          }
          then Working;

        transition nominal_selection_2
          accept priority2
          if (!ready1 || empty1) && ready2 && priority2.length() <= remainingCapacity
          do action {
            send priority2 to decision;
            send False to repeat2;
            assign remainingCapacity := remainingCapacity - priority2.length();
            assign ready1 := True;
            assign ready2 := False;
          }
          then Working;

        transition nominal_selection_3
          accept priority3
          if (!ready1 || empty1) && (!ready2 || empty2) && ready3 && priority3.length() <= remainingCapacity
          do action {
            send priority3 to decision;
            send False to repeat3;
            assign remainingCapacity := remainingCapacity - priority3.length();
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := False;
          }
          then Working;

        transition nominal_selection_4
          accept priority4
          if (!ready1 || empty1) && (!ready2 || empty2) && (!ready3 || empty3) && ready4
              && priority4.length() <= remainingCapacity
          do action {
            send priority4 to decision;
            send False to repeat4;
            assign remainingCapacity := remainingCapacity - priority4.length();
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := True;
            assign ready4 := False;
          }
          then Working;

        /*
         * Reset-Transition: Keiner ist Ready. Setze alle (inkl. niedrigere) auf True, ausser sich selbst
         * TOTAL: 4
         */

        transition reset_to_1
          accept priority1
          if !ready1 && !ready2 && !ready3 && !ready4 && priority1.length() <= remainingCapacity
          do action {
            send priority1 to output;
            send False to repeat1;
            assign remainingCapacity := remainingCapacity - priority1.lenth();
            assign ready1 := False;
            assign ready2 := True;
            assign ready3 := True;
            assign ready4 := True;
          }
          then Working;

        transition reset_to_2
          accept priority2
          if !ready1 && empty1 && !ready2 && !ready3 && !ready4 && priority2.length() <= remainingCapacity
          do action {
            send priority2 to output;
            send False to repeat2;
            assign remainingCapacity := remainingCapacity - priority2.lenth();
            assign ready1 := True;
            assign ready2 := False;
            assign ready3 := True;
            assign ready4 := True;
          }
          then Working;

        transition reset_to_3
          accept priority3
          if !ready1 && empty1 && !ready2 && empty2 && !ready3 && !ready4 && priority3.length() <= remainingCapacity
          do action {
            send priority3 to output;
            send False to repeat3;
            assign remainingCapacity := remainingCapacity - priority3.lenth();
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := False;
            assign ready4 := True;
          }
          then Working;

        transition reset_to_4
          accept priority4
          if !ready1 && empty1 && !ready2 && empty2 && !ready3 && empty3 && !ready4
              && priority4.length() <= remainingCapacity
          do action {
            send priority4 to output;
            send False to repeat4;
            assign remainingCapacity := remainingCapacity - priority4.lenth();
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := True;
            assign ready4 := False;
          }
          then Working;

        /*
         * Ready-Prioritäten sind alle leer, falle auf niedrigste Priorität zurück (Inversion). Es muss aber min. eine
         * Ready-Prio geben!
         * TOTAL: 4
         */

        transition empty_fallback_to_4
          accept priority4
          if ((ready1 && empty1) || !ready1) && ((ready2 && empty2) || !ready2) && ((ready3 && empty3) || !ready3) &&
              !ready4 && priority4.length() <= remainingCapacity && (ready1 || ready2 || ready3)
          do action {
            send priority4 to decision;
            send False to repeat4;
            assign remainingCapacity := remainingCapacity - priority4.length();
            assign ready1 := True;
            assign ready2 := True;
            assign ready3 := True;
          }
          then Working;

        transition empty_fallback_to_3
          accept priority3
          if ((ready1 && empty1) || !ready1) && ((ready2 && empty2) || !ready2) && !ready3 &&
              priority3.length() <= remainingCapacity && empty4
              && (ready1 || ready2 || ready4)
          do action {
            send priority3 to decision;
            send False to repeat3;
            assign remainingCapacity := remainingCapacity - priority3.length();
            assign ready1 := True;
            assign ready2 := True;
          }
          then Working;

        transition empty_fallback_to_2
          accept priority2
          if ((ready1 && empty1) || !ready1) && !ready2 && priority2.length() <= remainingCapacity && empty3 && empty4
              && (ready1 || ready3 || ready4)
          do action {
            send priority2 to decision;
            send False to repeat2;
            assign remainingCapacity := remainingCapacity - priority2.length();
            assign ready1 := True;
          }
          then Working;

        transition empty_fallback_to_1
          accept priority1
          if !ready1 && priority1.length() <= remainingCapacity && empty2 && empty3 && empty4
              && (ready2 || ready3 || ready4)
          do action {
            send priority1 to decision;
            send False to repeat1;
            assign remainingCapacity := remainingCapacity - priority1.length();
          }
          then Working;

        /*
         * Rest-Budget reicht nicht für höchste Ready-Priorität. Rein nach Prio entscheiden, Klappen belassen.
         * TOTAL: 3
         */

        transition fill_based_on_prio_alone_2
          accept priority2
          if ready1 && exceeded1 && priority2.length() <= remainingCapacity
          do action {
            send priority2 to decision;
            send False to repeat2;
            assign remainingCapacity := remainingCapacity - priority2.length();
          }

        transition fill_based_on_prio_alone_3
          accept priority2
          if ready1 && exceeded1 && ready2 && exceeded2 && priority3.length() <= remainingCapacity
          do action {
            send priority3 to decision;
            send False to repeat3;
            assign remainingCapacity := remainingCapacity - priority3.length();
          }

        transition fill_based_on_prio_alone_4
          accept priority4
          if ready1 && exceeded1 && ready2 && exceeded2 && ready3 && exceeded3
              && priority4.length() <= remainingCapacity
          do action {
            send priority4 to decision;
            send False to repeat4;
            assign remainingCapacity := remainingCapacity - priority4.length();
          }

        /*
         * Warten auf das Ende des Intervalls, niemand passt mehr rein. Sage allen, dass sie bis auf weiteres wieder-
         * holen müssen.
         * TOTAL: 4
         */

        transition wait_for_next_interval
          accept priority1
          if exceeded1 && exceeeded2 && exceeeded3 && exceeeded4
          do action {
            send True to repeat1;
          }

        // (...3 more)

        /*
         * Exceeding the limits. Setzt exceeded und beginnt das Wiederholen der Nachrichten
         * TOTAL: 4
         */

        transition exceeding_1
          accept priority1
          if priority1.length() > remainingCapacyity
          do action {
            send True to repeat1; // Oder garnicht behandeln (bzw. repeaten lassen), der Buffer könnte sich beim nächsten Tick wieder melden
            assign exceeded1 := True;
          }

        // (... 2 more)

        transition exceeding_4
          accept priority4
          if priority4.length() > remainingCapacyity
          do action {
            send True to repeat4;
            assign exceeded4 := True;
          }

        /*
         * Melden von empty
         * TOTAL: 4
         */

        transition empty_1
          accept isEmpty1
          do assign empty1 := isEmpty1
          then Working;

        // (...3 more)

        /*
         * TODO Fehlen mir nicht haufenweise repeats? Immer wenn priorityX kommt, aber einer der Guards nicht passt (aus
         * TODO einer der vielen Transitionen, die sich mit Messages befassen), dann muss die Nachricht wiederholt
         * TODO werden!!
         * TOTAL: TODO FUCK
         */

        /*
         * Im Nominal-Betrieb kriegen wir eine Nachricht, die grade nicht dran ist, dann muss diese Wiederholt werden
         * TOTAL: 4
         */

        transition nominal_repeat_1
          accept priority1
          if !ready1 && ((ready2 && !exceeded2 && !empty2) || (ready3 && !exceeded3 && !empty3)
              || (ready4 && !exceeded4 && !empty4))
          do send True to repeat1
          then Working;

        // (...3 more)

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
      part buffer1: Buffer;
      part buffer2: Buffer;
      part buffer3: Buffer;
      part buffer4: Buffer;
      part scheduler: PriorityScheduler;
      part io_out: IO_1x1;

      /* TODO Kaputt??
      connect user1 to io_in.input1;
      connect user2 to io_in.input2;
      connect user3 to io_in.input3;
      connect user4 to io_in.input4;

      connect io_in.output to router.input;

      connect router.priority1 to buffer1.input;
      connect router.priority2 to buffer2.input;
      connect router.priority3 to buffer3.input;
      connect router.priority4 to buffer4.input;

      connect buffer1.output to scheduler.priority1;
      connect buffer2.output to scheduler.priority2;
      connect buffer3.output to scheduler.priority3;
      connect buffer4.output to scheduler.priority4;
      connect buffer1.empty to scheduler.isEmpty1;
      connect buffer2.empty to scheduler.isEmpty2;
      connect buffer3.empty to scheduler.isEmpty3;
      connect buffer4.empty to scheduler.isEmpty4;

      connect scheduler.repeat1 to buffer1.repeat;
      connect scheduler.repeat2 to buffer2.repeat;
      connect scheduler.repeat3 to buffer3.repeat;
      connect scheduler.repeat4 to buffer4.repeat;

      connect scheduler.decision to io_out.in;
      connect io_out.output to output;
      */
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
