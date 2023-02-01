/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.timesync;

import de.monticore.lang.sysmlv2.generator.log.Log;

public class DelayPort<T> extends OutPort<T> implements IOutPort<T> {

  protected T nextValue;

  public DelayPort(String name) {
    super(name);
    this.nextValue = null;
  }

  public DelayPort() {
    this("");
  }

  @Override
  public void setValue(T value) {
    if(this.nextValue != null && this.nextValue != value) {
      Log.warn("Writing multiple times to port '" + this.getName() + "' in the same time slice.");
    }
    this.nextValue = value;
  }

  @Override
  public void tick() {
    super.tick();
    this.value = this.nextValue;
    this.nextValue = null;
    this.sync();
  }
}
