/* (c) https://github.com/MontiCore/monticore */
package StateDefReference {
  port def BooleanInput {
    in attribute val: boolean;
  }

  port def BooleanOutput {
    out attribute val: boolean;
  }

  part def Inverter {
    port i: BooleanInput;
    port o: BooleanOutput;

    exhibit state behavior: StateDef.AutomatonInverter(i = i::val, o = o::val);
  }
}
