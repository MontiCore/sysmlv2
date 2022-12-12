package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTSysMLSuccessionCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSpecializationCoCo;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class SuccessionCoCo implements SysMLActionsASTSysMLSuccessionCoCo {
  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  @Override
  public void check(ASTSysMLSuccession node) {
    var nonExistentSrc = node.getSrcDefinition().streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveActionDef(printName(t)).isEmpty()
            && node.getEnclosingScope().resolveActionUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for (var problem : nonExistentSrc) {
      Log.error("Could not find Action definition or usage with the name \"" + printName(problem) + "\" for "
          + node.getSrcDefinition().getName() + ".");
    }
    var nonExistentTgt = node.getSrcDefinition().streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveActionDef(printName(t)).isEmpty()
            && node.getEnclosingScope().resolveActionUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for (var problem : nonExistentTgt) {
      Log.error("Could not find Action definition or usage with the name \"" + printName(problem) + "\" for "
          + node.getSrcDefinition().getName() + ".");
    }
    if (node.getSrcDefinition().getName().equals(node.getTgtDefinition().getName())){
      Log.error("Source and target of succession must be different.");

    }
    //TODO add check that guard is a boolean
  }

}
