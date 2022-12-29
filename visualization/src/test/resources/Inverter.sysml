/* (c) https://github.com/MontiCore/monticore */
// spacing
package inverter {

  // ---------------------------  BDD  ---------------------------

  state def AutomatonInverter
    (in variableIn, out variableOut){

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


}

package inverter1 {

  import ScalarValues::*;

  package retrofit {
    port def Tuna {
      in tunaVal: Boolean;
    }
  }

  port def BooleanInput {
    in val: Boolean;
  }

  port def BooleanOutput {
    out val: Boolean;
  }

  part def Inverter {
    port i: BooleanInput;
    port o: BooleanOutput;

    state behavior: AutomatonInverter;
  }

  state def AutomatonInverter(in i: Boolean, out o: Boolean){
    entry;
      then S;

    state S;

    transition invert
      first S
      if true
      then S;
  }
}
