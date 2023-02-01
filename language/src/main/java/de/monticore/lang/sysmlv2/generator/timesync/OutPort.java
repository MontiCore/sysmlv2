/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.timesync;

import de.monticore.lang.sysmlv2.generator.log.Log;

import java.util.Collection;
import java.util.HashSet;

public class OutPort<T> extends Port<T> implements IOutPort<T> {

  protected Collection<IInPort<T>> observers;

  protected Collection<IInPort<T>> getObservers() {
    return this.observers;
  }

  public OutPort(String name) {
    this.name = name;
    this.observers = new HashSet<>(1);
  }

  public OutPort() {
    this("");
  }

  public String getName() {
    return this.name;
  }

  @Override
  public void setValue(T value) {
    if (this.value != null && this.value != value) {
      Log.warn("Writing multiple times to port '" + this.getName() + "' in the same time slice. "
        + "Overriding '" + this.value + "' with '" + value + "'." );
    }
    this.value = value;
    this.sync();
  }

  @Override
  public void sync() {
    this.getObservers().forEach(o -> o.update(this.value));
  }

  @Override
  public void connect(IInPort<T> port) {
    this.getObservers().add(port);
  }

  @Override
  public void tick() {
    super.tick();
    this.getObservers().forEach(IPort::tick);
  }
}
