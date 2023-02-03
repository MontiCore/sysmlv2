package de.monticore.lang.sysmlstates._ast;

public class ASTStateDef extends ASTStateDefTOP {
  boolean isAutomaton = false;
  public boolean getIsAutomaton(){
    return isAutomaton;
  }

  public void setIsAutomaton(boolean isAutomaton) {
    this.isAutomaton = isAutomaton;
  }
}
