package de.monticore.lang.sysmlv2.visitors;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;

import java.util.ArrayList;
import java.util.List;

public class SpecializationToSubsettingVisitor implements SysMLPartsVisitor2 {

  @Override
  public void endVisit(ASTAttributeUsage node) {
    List<ASTSpecialization> newList = new ArrayList<>();
    boolean changed = false;

    for (ASTSpecialization specialization : node.getSpecializationList()) {
      if (specialization instanceof ASTSysMLSpecialization) {
        ASTSysMLSpecialization sysMLSpecialization =
            (ASTSysMLSpecialization) specialization;

        ASTSysMLSubsetting subsetting =
            SysMLv2Mill.sysMLSubsettingBuilder()
                .setSuperTypesList(new ArrayList<>(
                    sysMLSpecialization.getSuperTypesList()))
                .build();

        if (sysMLSpecialization.isPresentSysMLCardinality()) {
          subsetting.setSysMLCardinality(
              sysMLSpecialization.getSysMLCardinality());
        }

        subsetting.set_SourcePositionStart(
            sysMLSpecialization.get_SourcePositionStart());
        subsetting.set_SourcePositionEnd(
            sysMLSpecialization.get_SourcePositionEnd());

        newList.add(subsetting);
        changed = true;
      }
      else {
        newList.add(specialization);
      }
    }

    if (changed) {
      node.setSpecializationList(newList);
    }
  }
}
