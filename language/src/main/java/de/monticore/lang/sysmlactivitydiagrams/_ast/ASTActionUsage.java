package de.monticore.lang.sysmlactivitydiagrams._ast;

import java.util.Optional;

// Name der action usage kann nicht direkt von MontiCore abgeleitet werden, deswegen ist die Oberklasse abstrakt
public class ASTActionUsage extends ASTActionUsageTOP {
  protected Optional<String> name = Optional.empty();

  public boolean isPresentName() {
    return name.isPresent();
  }

  public void setName(String name) {
    this.name = Optional.of(name);
  }

  @Override
  public String getName() {
    return name.get();
  }
}
