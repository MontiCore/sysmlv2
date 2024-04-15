package de.monticore.lang.sysmlstates._ast;

public class ASTStateUsage extends ASTStateUsageTOP {

  public boolean isPresentEntryAction() {
    return !getEntryActionList().isEmpty();
  }

  public ASTEntryAction getEntryAction() {
    return getEntryAction(0);
  }

  public boolean isPresentDoAction() {
    return getSysMLElementList().stream().anyMatch(s -> s instanceof ASTDoAction);
  }

}
