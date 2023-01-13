package de.monticore.lang.sysmlparts._ast;

import java.util.List;
import java.util.stream.Stream;

public class ASTPartUsage extends ASTPartUsageTOP {

  protected List<ASTPartDef> transitiveDefSupertypes
      = new java.util.ArrayList<>();
  protected List<ASTPartUsage> transitiveUsageSupertypes
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
  public Stream<ASTPartUsage> streamTransitiveUsageSupertypes() {
    return this.getTransitiveUsageSupertypes().stream();

  }

  public void setTransitiveUsageSupertypes(List<ASTPartUsage> transitiveUsageSupertypes) {

    this.transitiveUsageSupertypes = transitiveUsageSupertypes;

  }

  public List<ASTPartUsage> getTransitiveUsageSupertypes() {

    return this.transitiveUsageSupertypes;
  }
}
