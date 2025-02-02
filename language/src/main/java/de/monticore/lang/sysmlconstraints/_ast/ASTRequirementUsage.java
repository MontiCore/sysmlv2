package de.monticore.lang.sysmlconstraints._ast;

public class ASTRequirementUsage extends ASTRequirementUsageTOP {

  /**
   * There can be [0..1] explicit subjects. This convenience methods checks if there is one which can then be retrived
   * using {@link #getRequirementSubject()}
   */
  public boolean isPresentRequirementSubject() {
    return getSysMLElementList()
        .stream()
        .anyMatch(e -> e instanceof ASTRequirementSubject);
  }

  /**
   * Convenience methods to retrieve the subject, if there is one. Check using {@link #isPresentRequirementSubject()}.
   */
  public ASTRequirementSubject getRequirementSubject() {
    return getSysMLElementList()
        .stream()
        .filter(e -> e instanceof ASTRequirementSubject)
        .map(e -> (ASTRequirementSubject)e)
        .findFirst()
        .get();
  }

}
