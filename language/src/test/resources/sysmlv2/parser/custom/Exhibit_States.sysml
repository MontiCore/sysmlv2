port def BooleanInput {
  in val: Boolean;
}

port def BooleanOutput {
  out val: Boolean;
}

part def Component {
  port i: BooleanInput;
  port o: BooleanOutput;

  //Referenziert stateUsage (behavior), welche schon irgendwo (siehe unten) definiert sein muss. Bindet Parameter
  exhibit behavior {
    in i = i::val;
    out o = o::val;
  }

  //Alternative: State Usage direkt an dieser Stelle mit deklarieren
  exhibit state behavior2: AutomatonDef(i = i::val, o = o::val);

  //Alternative2: Referenziert stateUsage (behavior3), welche schon irgendwo (siehe unten) definiert sein muss.
  //Parameter werden von stateUsage übernommen.
  exhibit behavior3;

  //Alternative3: Ad-Hoc Usage
  exhibit state {
    state Zustand;
  }

  state behavior3: AutomatonDef(i = i::val, o = o::val);
}

state def AutomatonDef(in i: Boolean, out o: Boolean);

state behavior: AutomatonDef;
