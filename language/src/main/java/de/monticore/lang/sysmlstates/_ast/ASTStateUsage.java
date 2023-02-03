package de.monticore.lang.sysmlstates._ast;

public class ASTStateUsage extends ASTStateUsageTOP {
  boolean isAutomaton = false;

  public boolean getIsAutomaton() {
    return isAutomaton;
  }

  public void setIsAutomaton(boolean isAutomaton) {
    this.isAutomaton = isAutomaton;
  }
}
