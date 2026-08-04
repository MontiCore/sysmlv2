package de.monticore.lang.sysmlconstraints._ast;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLReference;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint references are syntactic sugar for constraint usages that only reference
 * other usages.
 */
public class ASTConstraintReference extends ASTConstraintReferenceTOP {
  @Override
  public List<ASTSpecialization> getSpecializationList() {
    List<ASTSpecialization> res = new ArrayList<>(super.getSpecializationList());
    ASTMCQualifiedType type = SysMLv2Mill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(getMCQualifiedName())
        .set_SourcePositionStart(getMCQualifiedName().get_SourcePositionStart())
        .set_SourcePositionEnd(getMCQualifiedName().get_SourcePositionEnd())
        .build();
    type.setEnclosingScope(getMCQualifiedName().getEnclosingScope());

    ASTSysMLReference reference = SysMLv2Mill.sysMLReferenceBuilder()
        .setSuperTypesList(List.of(type))
        .set_SourcePositionStart(getMCQualifiedName().get_SourcePositionStart())
        .set_SourcePositionEnd(getMCQualifiedName().get_SourcePositionEnd())
        .build();
    reference.setEnclosingScope(getMCQualifiedName().getEnclosingScope());

    res.add(reference);
    return res;
  }
}
