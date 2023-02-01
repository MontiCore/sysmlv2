/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.timesync;

public interface IPort<T> {

  /**
   * switch to the next time slice
   */
  void tick();

  /**
   * @return the value present at the current time slice
   */
  T getValue();
}
