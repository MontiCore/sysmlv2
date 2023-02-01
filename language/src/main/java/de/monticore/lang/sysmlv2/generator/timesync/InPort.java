/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.timesync;

public class InPort<T> extends Port<T> implements IInPort<T> {

  boolean synced;

  public InPort(String name) {
    this.name = name;
    this.synced = false;
  }

  public InPort() {
    this("");
  }

  @Override
  public boolean isSynced() {
    return this.synced;
  }

  @Override
  public void update(T value) {
    this.value = value;
    this.synced = true;
  }

  @Override
  public void tick() {
    super.tick();
    this.synced = false;
  }
}
