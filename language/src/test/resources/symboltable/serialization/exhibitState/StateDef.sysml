/* (c) https://github.com/MontiCore/monticore */
package StateDef {

  state def AutomatonInverter(in i: boolean, out o: boolean){
    entry;
      then S;

    state S;

    transition invert
      first S
      accept i
      if true
      do action { send !i to o; }
      then S;
  }

}
