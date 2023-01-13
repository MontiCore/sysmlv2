package de.monticore.lang.sysmlparts._ast;

import java.util.List;
import java.util.stream.Stream;

public class ASTPartDef extends ASTPartDefTOP {

  protected java.util.List<ASTPartDef> transitiveDefSupertypes
      = new java.util.ArrayList<>();

  public Stream<ASTPartDef> streamTransitiveDefSupertypes() {
    return this.getTransitiveDefSupertypes().stream();

  }

  public void setTransitiveDefSupertypes(List<ASTPartDef> transitiveDefSupertypes) {

    this.transitiveDefSupertypes = transitiveDefSupertypes;

  }

  public List<ASTPartDef> getTransitiveDefSupertypes() {

    return this.transitiveDefSupertypes;
  }

}
