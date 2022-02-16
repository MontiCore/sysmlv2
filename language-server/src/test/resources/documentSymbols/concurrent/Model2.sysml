package inverter_1 {

  // ---------------------------  BDD  ---------------------------

  state def AutomatonInverter_2
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


}

package avionic1 {
  state def AutomatonInverter
    (in variableIn : Boolean, out variableOut : Boolean){

      entry; then StateInitial;

      state StateInitial;

      transition invert
        first StateInitial
        if true
        do send !variableIn to variableOut
        then StateInitial;
  }
}
