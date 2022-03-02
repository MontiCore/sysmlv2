/**
 * [CONFIDENTIAL] Handle with care
 */
package 'Data Link Upload Feed - A Case Study' {

  port def DecisionInputs {
    /* Ob der Buffer leer ist, also es nichts zu senden gibt */
    in empty1: Boolean;
    in empty2: Boolean;
    in empty3: Boolean;
    in empty4: Boolean;

    /* Ob die Klasse ihre Min.-Latenz noch erfüllen kann */
    in inLimit1: Boolean;
    in inLimit2: Boolean;
    in inLimit3: Boolean;
    in inLimit4: Boolean;

    /* "ExceedTraffic" berechnet diesen Bool aus dem PacketCount, der wiederum aus Outputs::UpdateSize stammt.
     * Dort wird er aus PacketSize und Reset berechnet - vmtl durch einen Zähler der inkrementiert und resetet wird */
    in exceeded: Boolean;

    /* DataLink signalisiert, dass erstmal nichts mehr geschrieben werden kann (s. Fortiss-Spezifikationsdokument) */
    in locked: Boolean;
  }

  port def DecisionOutputs {
    out selectedBuffer: Integer;
  }

  /* TODO Herausfinden, ob und wie man Kanäle in Port Usages einzeln verbinden kann */

  part computer {
    part dataLinkUploadFeed {
      part selectBuffer {
        part decision {
          port i: DecisionInputs;
          port o: DecisionOutputs;

          // TODO Wie zufällig initialisieren?
          value empty1 : Boolean;
          value empty2 : Boolean;
          value empty3 : Boolean;
          value empty4 : Boolean;
          value inLimit1 : Boolean;
          value inLimit2 : Boolean;
          value inLimit3 : Boolean;
          value inLimit4 : Boolean;

          // TODO klären ob der Automat Parameter braucht und wie man diese mit den Ports verbindet
          exhibit state behavior {
            entry;
            then Working; // TODO SysML v2 Official erlaubt keinen Nicht-determ. beim initialen Zustand

            state Working;
            state Locked;
            state Exceeded;
            state LockedAndExceeded;

            /*
             * Transitionen in LockedAndExceeded, die nur die Arrays anpassen
             * TOTAL: 8
             */

            transition no_op_1
              first LockedAndExceeded
              accept i.inLimit1
              do action { assign inLimit1 := i.inLimit1; }
              then LockedAndExceeded;

            // ... (7 more)

            /*
             * Transitionen in Locked, die nur die Arrays anpassen
             * TOTAL: 8
             */

            transition no_op_9
              first Locked
              accept i.inLimit1
              do action { assign inLimit1 := i.inLimit1; }
              then Locked;

            // ... (7 more)

            /*
             * Transitionen in Exceeded, die nur die Arrays anpassen
             * TOTAL: 8
             */

            transition no_op_17
              first Exceeded
              accept i.inLimit1
              do action { assign inLimit1 := i.inLimit1; }
              then Exceeded;

            // ... (7 more)

            /*
             * Transitionen von LockedAndExceeded nach Locked oder Exceeded
             * TOTAL: 2
             */

            transition no_op_25
              first LockedAndExceeded
              accept i.exceeded
              if !i.exceeded
              then Locked;

            transition no_op_26
              first LockedAndExceeded
              accept i.locked
              if !i.locked
              then Exceeded;

            /*
             * Transitionen von Locked oder Exceeded nach LockedAndExceeded
             * TOTAL: 2
             */

            transition no_op_27
              first Locked
              accept i.exceeded
              if i.exceeded
              then LockedAndExceeded;

            transition no_op_28
              first Exceeded
              accept i.locked
              if i.locked
              then LockedAndExceeded;

            /*
             * Transitionen von Locked oder Exceeded nach Working
             * TOTAL: 2
             */

            transition get_to_work_1
              first Locked
              accept i.locked
              if (!i.locked && (!empty0 && inLimit0 || !empty1 && inLimit1 || !empty2 && inLimit2
                  || !empty3 && inLimit3))
              do
                  send (
                      if !empty0 && inLimit0 then 1
                      else if !empty1 && inLimit1 then 2
                      else if !empty2 && inLimit2 then 3
                      else 4
                  ) to o.selectedBuffer
              then Working;

            transition get_to_work_2
              first Exceeded
              accept i.exceeded
              if !i.exceeded && (!empty0 && inLimit0 || !empty1 && inLimit1 || !empty2 && inLimit2
                  || !empty3 && inLimit3)
              do
                  send (
                      if !empty0 && inLimit0 then 1
                      else if !empty1 && inLimit1 then 2
                      else if !empty2 && inLimit2 then 3
                      else 4
                  ) to o.selectedBuffer
              then Working;

            /*
             * Transitionen von Working nach Locked oder Exceeded
             * TOTAL: 2
             */

            transition stop_working_1
              first Working
              accept i.locked
              if i.locked
              then Locked;

            transition stop_working_2
              first Working
              accept i.exceeded
              if i.exceeded
              then Exceeded;

            /*
             * Transitionen in Working
             * Total: 8
             */

            transition updating_limits_while_working_1
              first Working
              accept i.inLimit1
              if !empty0 && inLimit0 || !empty1 && i.inLimit1 || !empty2 && inLimit2 || !empty3 && inLimit3
              do action {
                send (
                    if !empty0 && inLimit0 then 1
                    else if !empty1 && inLimit1 then 2
                    else if !empty2 && inLimit2 then 3
                    else 4
                ) to o.selectedBuffer;
                send i.inLimit1 to inLimit1;
              }
              then Working;

            // ... (3 more)

            transition updating_limits_while_working_5
              first Working
              accept i.empty1
              if !empty0 && inLimit0 || !i.empty1 && i.inLimit1 || !empty2 && inLimit2 || !empty3 && inLimit3
              do action {
                send (
                    if !empty0 && inLimit0 then 1
                    else if !empty1 && inLimit1 then 2
                    else if !empty2 && inLimit2 then 3
                    else 4
                ) to o.selectedBuffer;
                send i.empty1 to empty1;
              }
              then Working;

            // ... (3 more)

            // ** TOTAL: 40 ***
          }
        }
      }
    }
  }

  requirement def <"R1"> Priority  {
    subject behavior = computer.dataLinkUploadFeed.selectBuffer.decision.behavior;

    assume constraint { !i.locked && !i.exceeded }
    require constraint {
      !empty1 && inLimit1 implies o.selectedBuffer == i
    }
    require constraint {
      !empty2 && inLimit2 && (empty1 || !inLimit1) implies o.selectedBuffer == 2
    }
    require constraint {
      !empty3 && inLimit3 && (empty1 || !inLimit1) && (empty2 || !inLimit2) implies o.selectedBuffer == 3
    }
    require constraint {
      !empty4 && inLimit4 && (empty1 || !inLimit1) && (empty2 || !inLimit2) && (empty3 || !inLimit3) implies o.selectedBuffer == 4
    }
  }

  requirement def <"R2"> LockedOrExceeded {
    subject behavior = computer.dataLinkUploadFeed.selectBuffer.decision.behavior;

    assume constraint { i.locked || i.exceeded }
    require constraint { not (exists x in {1,2,3,4}: o.selectedBuffer == x) } // TODO spezielles "empty" nötig?
  }
}
