package de.monticore.lang.sysmlv2._ast;

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
