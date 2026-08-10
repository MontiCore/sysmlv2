/* (c) https://github.com/MontiCore/monticore */
package WithoutBody;



package WithNestation {

  constraint def A {
    private import SI::*;
    in attribute a : MassValue;

    a >= 0 [kg]
  }
}
