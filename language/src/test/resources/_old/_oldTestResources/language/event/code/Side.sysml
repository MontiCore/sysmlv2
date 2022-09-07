package code {

  /*enum Status {
    Confirmed, Inhibited, Listening, Waiting;
  }*/

  port def BooleanInPort {
    in val: Boolean;
  }

  port def BooleanOutPort {
    out val: Boolean;
  }

  port def StatusInPort {
    in val: StatusEnum;
  }

  port def StatusOutPort {
    out val: StatusEnum;
  }

  part def Side(initialSide: Boolean) {
    port switch:      BooleanInPort;
    port statusI:      StatusInPort;
    port disturbance: BooleanInPort;
    port statusO:      StatusOutPort;

    state behavior: SideAutomaton(
      switch = switch::val,
      statusI = statusI::val,
      disturbance = disturbance::val,
      statusO = statusO::val,
      initialSide);
  }

  state def SideAutomaton(
    in switch:      Boolean,
    in statusI:     StatusEnum,
    in disturbance: Boolean,
    out statusO:    StatusEnum,
    initialSide: Boolean)
  {
    entry
      action { send EConfirmed to statusO; send false to disturbed; }
      then Confirmed;

    // Noch nicht möglich
    /*
     * entry;
     *   if initialSide
     *   action { send EConfirmed to statusO; }
     *   then Confirmed;
     *
     * entry
     *   if !initialSide
     *   action { send EListening to statusO; }
     *   then Listening;
     */

    value disturbed: Boolean;

    state Confirmed;
    state Inhibited;
    state Listening;
    state Waiting;

    transition
      first Confirmed
      accept disturbance
      do action { send disturbance to disturbed; }
      then Confirmed;

   /* transition
    *   first Inhibited
    *   accept disturbance
    *   do action { send disturbance to disturbed; }
    *   then Inhibited;
    *
    * transition
    *   first Listening
    *   accept disturbance
    *   do action { send disturbance to disturbed; }
    *   then Listening;
    *
    * transition
    *   first Waiting
    *   accept disturbance
    *   do action { send disturbance to disturbed; }
    *   then Waiting;
    */

	  /* 1. Status transitions */

    /* a. From Confirmed */

    transition
      first Confirmed
      accept statusI
      if !disturbed && statusI == EWaiting
      do action { send EInhibited to statusO; }
      then Inhibited;

    transition
      first Confirmed
      accept statusI
      if !(!disturbed && statusI == EWaiting)
      do action { send EConfirmed to statusO; }
      then Confirmed;

    /* b. From Inhibited */

    transition
      first Inhibited
      accept statusI
      if !disturbed && statusI == EConfirmed
      do action { send EListening to statusO; }
      then Listening;

    transition
      first Inhibited
      accept statusI
      if !(!disturbed && statusI == EConfirmed)
      do action { send EInhibited to statusO; }
      then Inhibited;

	  /* c. From Waiting */

    transition
      first Waiting
      accept statusI
      if !disturbed && statusI == EInhibited
      do action { send EConfirmed to statusO; }
      then Confirmed;

    transition
      first Waiting
      accept statusI
      if !(!disturbed && statusI == EInhibited)
      do action { send EWaiting to statusO; }
      then Waiting;

	  /* 2. Switch transitions */

    /* a. From Listening */

    transition
      first Listening
      accept switch
      if !disturbed && switch
      do action { send EWaiting to statusO;}
      then Waiting;

    transition
      first Listening
      accept switch
      if disturbed || !switch
      do action { send EListening to statusO; }
      then Listening;

    /* 3. Tick */

	  transition
      first Confirmed
      accept Tick
      then Confirmed;

	  transition
      first Inhibited
      accept Tick
      then Inhibited;

	  transition
      first Listening
      accept Tick
      then Listening;

	  transition
      first Waiting
      accept Tick
      then Waiting;

	  /* 4. Technical Details - Completion */

    transition
      first Listening
      accept statusI
      then Listening;

    transition
      first Confirmed
      accept switch
      then Confirmed;

    transition
      first Inhibited
      accept switch
      then Inhibited;

    transition
      first Waiting
      accept switch
      then Waiting;
  }
}
