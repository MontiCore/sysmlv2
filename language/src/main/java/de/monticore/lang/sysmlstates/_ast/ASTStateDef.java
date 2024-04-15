package de.monticore.lang.sysmlstates._ast;

public class ASTStateDef extends ASTStateDefTOP {

  public boolean isPresentEntryAction() {
    return getSysMLElementList().stream().anyMatch(s -> s instanceof ASTEntryAction);
  }

  public ASTEntryAction getEntryAction() {
    return getEntryAction(0);
  }

  public boolean isPresentDoAction() {
    return getSysMLElementList().stream().anyMatch(s -> s instanceof ASTDoAction);
  }

}
