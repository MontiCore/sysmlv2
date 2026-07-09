package de.monticore.lang.sysmlv2.visitors;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLType;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpecializationToSubsettingVisitor implements SysMLBasisVisitor2, SysMLPartsVisitor2 {

  @Override
  public void endVisit(ASTSysMLElement node) {
    
    if (node instanceof ASTSysMLType) {
      return;
    }

    replaceWrongSpecializations(node);
  }

  @Override
  public void endVisit(ASTAttributeUsage node) {

    replaceWrongSpecializations(node);

  }

  @SuppressWarnings("unchecked")
  protected void replaceWrongSpecializations(ASTSysMLElement node) {

    try {

      Method getter = node.getClass().getMethod("getSpecializationList");
      Method setter = node.getClass().getMethod("setSpecializationList", List.class);

      List<ASTSpecialization> oldList = (List<ASTSpecialization>) getter.invoke(node);

      List<ASTSpecialization> newList = new ArrayList<>();
      boolean changed = false;

      for (ASTSpecialization specialization : oldList) {

        if (specialization instanceof ASTSysMLSpecialization) {

          ASTSysMLSpecialization sysMLSpecialization = (ASTSysMLSpecialization) specialization;
          newList.add(createSubsetting(sysMLSpecialization));
          changed = true;

        } else {
          newList.add(specialization);
        }
      }

      if (changed) {
        setter.invoke(node, newList);
      }
    }
    catch (NoSuchMethodException ignored) {
      // Some SysML elements do not have a specializationList.
    }
    catch (IllegalAccessException | InvocationTargetException e) {

      throw new IllegalStateException("Could not rewrite specialization list of " + node.getClass().getSimpleName(), e);

    }
  }

  protected ASTSysMLSubsetting createSubsetting(ASTSysMLSpecialization specialization) {
    ASTSysMLSubsetting subsetting = SysMLv2Mill.sysMLSubsettingBuilder().setSuperTypesList(new ArrayList<>(specialization.getSuperTypesList())).build();

    if (specialization.isPresentSysMLCardinality()) {

      subsetting.setSysMLCardinality(specialization.getSysMLCardinality());

    }

    subsetting.set_SourcePositionStart(specialization.get_SourcePositionStart());
    subsetting.set_SourcePositionEnd(specialization.get_SourcePositionEnd());
    return subsetting;
  }

  public static void apply(ASTSysMLModel ast) {

    SpecializationToSubsettingVisitor visitor = new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLBasis(visitor);
    traverser.add4SysMLParts(visitor);
    ast.accept(traverser);

  }
}
