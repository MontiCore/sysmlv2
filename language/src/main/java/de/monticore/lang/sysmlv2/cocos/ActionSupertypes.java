/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

// Missing specializations are handled by SpecializationExistsTC3.
// This CoCo only reports existing specializations with invalid action type.

public class ActionSupertypes
    implements SysMLActionsASTActionDefCoCo, SysMLActionsASTActionUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType();
  }

  /**
   * Checks that existing super types (specializations) of an ActionDef are
   * Action definitions. Missing super types are ignored because they are
   * reported by SpecializationExistsTC3.
   */
  @Override
  public void check(ASTActionDef node) {
    var invalidSupertypes = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> {
          String name = printName(t);

          boolean exists =
              node.getEnclosingScope().resolveType(name).isPresent()
                  || node.getEnclosingScope().resolveActionDef(name).isPresent()
                  || node.getEnclosingScope().resolveActionUsage(
                  name).isPresent();

          boolean isActionDef =
              node.getEnclosingScope().resolveActionDef(name).isPresent();

          return exists && !isActionDef;
        })
        .collect(Collectors.toList());

    for (var problem : invalidSupertypes) {
      Log.error("0x10017 Specialization \"" + printName(problem)
          + "\" is not an Action definition.");
    }
  }

  /**
   * Checks that existing super types (specializations) of an ActionUsage are
   * Action definitions or usages. Missing super types are ignored because they
   * are reported by SpecializationExistsTC3.
   */
  @Override
  public void check(ASTActionUsage node) {
    var invalidSupertypes = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> {
          String name = printName(t);

          boolean exists =
              node.getEnclosingScope().resolveType(name).isPresent()
                  || node.getEnclosingScope().resolveActionDef(name).isPresent()
                  || node.getEnclosingScope().resolveActionUsage(
                  name).isPresent();

          boolean isAction =
              node.getEnclosingScope().resolveActionDef(name).isPresent()
                  || node.getEnclosingScope().resolveActionUsage(
                  name).isPresent();

          return exists && !isAction;
        })
        .collect(Collectors.toList());

    for (var problem : invalidSupertypes) {
      Log.error("0x10020 Specialization \"" + printName(problem)
          + "\" is not an Action definition or usage.");
    }
  }
}
