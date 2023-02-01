/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.timesync;

public interface IInPort<T> extends IPort<T> {

  /**
   * @return whether this was already synchronized with the observed output
   * port in the current time slice
   */
  boolean isSynced();

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param msg the msg sent via the observed output port
   */
  void update(T msg);
}
