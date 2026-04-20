package de.monticore.lang.sysmlbasis.visitors;

import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import java.util.Optional;

public class CalcReturnSpecVisitor implements SysMLBasisVisitor2 {
  protected ASTSpecialization specializationReturn;

  @Override
  public void visit(ASTAnonymousUsage node) {
    if (specializationReturn == null && node.getModifier().isReturn()
        && !node.getSpecializationList().isEmpty()) {
      specializationReturn = node.getSpecialization(0);
    }
  }

  public Optional<ASTSpecialization> getSpecializationReturn() {
    return Optional.ofNullable(specializationReturn);
  }
}
