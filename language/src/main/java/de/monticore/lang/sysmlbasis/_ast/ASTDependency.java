package de.monticore.lang.sysmlbasis._ast;

public class ASTDependency extends ASTDependencyTOP {

  public boolean isRefinementDependency() {
    return getUserDefinedKeywordList().stream()
        .map(keyword -> keyword.getMCQualifiedName().toString())
        .anyMatch("refinement"::equals);
  }
}
