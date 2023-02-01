/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.timesync;

public abstract class Port<T> implements IPort<T> {

  protected T value;

  protected String name;

  protected String getName() {
    return this.name;
  }

  @Override
  public T getValue() {
    return this.value;
  }

  @Override
  public void tick() {
    this.value = null;
  }
}
