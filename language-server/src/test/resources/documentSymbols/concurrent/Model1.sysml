/* (c) https://github.com/MontiCore/monticore */
package Inverter {

  port def BoolInPort {
    in value bIn : boolean;
  }

  part def Side(initialSide: boolean) {
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
    (in variableIn : boolean, out variableOut : boolean){

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
 state def vald(in variableI: boolean);

}

package inverter {
	port def BoolOutPort {
    in value bOut : boolean;
  }
}
