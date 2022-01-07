/* (c) https://github.com/MontiCore/monticore */
/**
 * Checks PortUsage Symbols - see PortUsageSymbolTest
 */
package example {

  port def Signal {
    in val: Boolean;
  }

  part def SignalConverter {
    port i: Signal;
    port o: ~Signal;

    constraint equality {
      i.val == o.val
    }
  }

}
