package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlstates._ast.ASTStateUsage;

import java.util.List;
import java.util.stream.Stream;

public class ASTPartUsage extends ASTPartUsageTOP {

  protected List<ASTPartDef> transitiveDefSupertypes
      = new java.util.ArrayList<>();

  protected List<ASTPartUsage> transitiveUsageSupertypes
      = new java.util.ArrayList<>();

  protected ASTStateUsage automaton;

  public ASTStateUsage getAutomaton() {
    return automaton;
  }

  public void setAutomaton(ASTStateUsage automaton) {
    this.automaton = automaton;
  }

  public boolean hasAutomaton() {
    return getAutomaton() != null;
  }

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
