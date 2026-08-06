package de.monticore.lang.sysmlconstraints._ast;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLReference;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.List;

public class ASTRequirementSatisfyReference extends ASTRequirementSatisfyReferenceTOP {
  @Override
  public List<ASTSpecialization> getSpecializationList() {
    List<ASTSpecialization> res = new ArrayList<>(super.getSpecializationList());
    ASTMCQualifiedType type = SysMLv2Mill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(getReq())
        .set_SourcePositionStart(getReq().get_SourcePositionStart())
        .set_SourcePositionEnd(getReq().get_SourcePositionEnd())
        .build();

    ASTSysMLReference reference = SysMLv2Mill.sysMLReferenceBuilder()
        .setSuperTypesList(List.of(type))
        .set_SourcePositionStart(getReq().get_SourcePositionStart())
        .set_SourcePositionEnd(getReq().get_SourcePositionEnd())
        .build();

    // because this is accessible even without symboltable gen, only set when scope is actually there
    var delegateEnclosing = getReq().getEnclosingScope();
    if (delegateEnclosing != null) {
      type.setEnclosingScope(delegateEnclosing);
      reference.setEnclosingScope(delegateEnclosing);
    }

    res.add(reference);
    return res;
  }

  @Override
  public boolean isSatisfy() {
    return true;
  }
}
