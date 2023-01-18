package de.monticore.lang.sysmlparts._ast;

import java.util.List;
import java.util.stream.Stream;

public class ASTAttributeDef extends ASTAttributeDefTOP {

  protected List<ASTAttributeDef> transitiveDefSupertypes
      = new java.util.ArrayList<>();

  public Stream<ASTAttributeDef> streamTransitiveDefSupertypes() {
    return this.getTransitiveDefSupertypes().stream();

  }

  public void setTransitiveDefSupertypes(List<ASTAttributeDef> transitiveDefSupertypes) {

    this.transitiveDefSupertypes = transitiveDefSupertypes;

  }

  public List<ASTAttributeDef> getTransitiveDefSupertypes() {

    return this.transitiveDefSupertypes;
  }

}
