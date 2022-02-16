package Inverter {

  port def BoolInPort {
    in value bIn : Boolean;
  }

  part def Side(initialSide: Boolean) {
    port switch:      BooleanInPort;
    port status:      StatusInPort;
    port disturbance: BooleanInPort;
    port status:      StatusOutPort;
    state behavior: SideAutomaton(
      switch = switch::val,
      status = status::val,
      disturbance = disturbance::val,
      status = status::val,
      initialSide);
  }



  state def AutomatonInverter_1
    (in variableIn : Boolean, out variableOut : Boolean){

      entry; then StateInitial;

      state StateInitial;

      transition invert
        first StateInitial
        if true
        do send !variableIn to variableOut
        then StateInitial;
  }

  // ---------------------------  IBD  ---------------------------


  // ---------------------------  AD  ---------------------------
 state def vald(in variableI: Boolean);

}

package inverter {
	port def BoolOutPort {
    in value bOut : Boolean;
  }
}
