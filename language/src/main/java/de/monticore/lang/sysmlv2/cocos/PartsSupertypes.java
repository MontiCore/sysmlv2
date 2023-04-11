/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTDefSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._ast.ASTUsageSpecialization;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

// TODO Muss mit SpecialiationExists zusammenspielen, also darf der nicht anschlagen, wenn garkein Type existiert,
// sondern nur, wenn zwar einer existiert, es aber keine PartsDef/PartsUsage ist
public class PartsSupertypes implements SysMLPartsASTPartDefCoCo, SysMLPartsASTPartUsageCoCo, SysMLPartsASTPortDefCoCo,
    SysMLPartsASTPortUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that all super types (specializations) exist. They need to be part definitions.
   */
  @Override
  public void check(ASTPartDef node) {
    var nonExistent = node.streamDefSpecializations().filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(ASTDefSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolvePartDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("Could not find part definition \"" + printName(problem) + "\".");
    }
    var numberOfOtherSpecialications = node.streamDefSpecializations().filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(ASTDefSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolvePartDef(printName(t)).isEmpty())
        .count();
    if(numberOfOtherSpecialications != 0)
      Log.error("Part def is not allowed to use typing or redefinition specialization, in part def " + node.getName() + ".");
  }

  /**
   * Check that all super types (specializations) exist. They might be part definitions or usages.
   */
  @Override
  public void check(ASTPartUsage node) {
    var nonExistent = node.streamUsageSpecializations().filter(t -> t instanceof ASTSysMLSubsetting | t instanceof ASTSysMLRedefinition)
        .flatMap(ASTUsageSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolvePartUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());
    for(var problem: nonExistent) {
      Log.error("Could not find part usage with the name \"" + printName(problem) + "\".");
    }
    var nonExistentType = node.streamUsageSpecializations().filter(t -> t instanceof ASTSysMLTyping)
        .flatMap(ASTUsageSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolvePartDef(printName(t)).isEmpty())
        .collect(Collectors.toList());
    for(var problem: nonExistentType) {
      Log.error("Could not find part def with the name \"" + printName(problem) + "\".");
    }

  }
  @Override
  public void check(ASTPortDef node) {
    var nonExistent = node.streamDefSpecializations()
        .flatMap(ASTDefSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolvePortDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("Could not find part definition \"" + printName(problem) + "\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be part definitions or usages.
   */
  @Override
  public void check(ASTPortUsage node) {
    var nonExistent = node.streamUsageSpecializations()
        .flatMap(ASTUsageSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolvePortDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("Could not find part definition with the name \"" + printName(problem) + "\".");
    }
  }
}
