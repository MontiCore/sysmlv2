package de.monticore.lang.sysmlparts._ast;

import java.util.List;
import java.util.stream.Stream;

public class ASTPartUsage extends ASTPartUsageTOP {

  protected List<ASTPartDef> transitiveSupertypes
      = new java.util.ArrayList<>();

  public Stream<ASTPartDef> streamTransitiveSupertypes() {
    return this.getTransitiveSupertypes().stream();

  }

  public void setTransitiveSupertypes(List<ASTPartDef> transitiveSupertypes) {

    this.transitiveSupertypes = transitiveSupertypes;

  }

  public List<ASTPartDef> getTransitiveSupertypes() {

    return this.transitiveSupertypes;
  }

}
