/* (c) https://github.com/MontiCore/monticore */
package inverter {

  // ---------------------------  BDD  ---------------------------

  state def AutomatonInverter23
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
