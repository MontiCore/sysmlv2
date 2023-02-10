package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlstates._ast.ASTStateUsage;

import java.util.List;
import java.util.stream.Stream;

public class ASTPartDef extends ASTPartDefTOP {

  protected java.util.List<ASTPartDef> transitiveDefSupertypes
      = new java.util.ArrayList<>();

  public Stream<ASTPartDef> streamTransitiveDefSupertypes() {
    return this.getTransitiveDefSupertypes().stream();

  }

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

  public void setTransitiveDefSupertypes(List<ASTPartDef> transitiveDefSupertypes) {

    this.transitiveDefSupertypes = transitiveDefSupertypes;

  }

  public List<ASTPartDef> getTransitiveDefSupertypes() {

    return this.transitiveDefSupertypes;
  }

}
