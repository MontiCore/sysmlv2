/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

// TODO Muss mit SpecialiationExists zusammenspielen, also darf der nicht anschlagen, wenn garkein Type existiert,
// sondern nur, wenn zwar einer existiert, es aber keine StateDef/StateUsage ist
public class StateSupertypes implements SysMLStatesASTStateDefCoCo, SysMLStatesASTStateUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that all super types (specializations) exist. They need to be state definitions.
   */
  @Override
  public void check(ASTStateDef node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(ASTSpecialization::streamSuperTypes)
        .filter(t -> node.getEnclosingScope().resolveStateDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for (var problem : nonExistent) {
      Log.error("Could not find state definition \"" + printName(problem) + "\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be state definitions or usages.
   */
  @Override
  public void check(ASTStateUsage node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(ASTSpecialization::streamSuperTypes).filter(t-> !(t instanceof ASTSysMLTyping))
        .filter(t -> node.getEnclosingScope().resolveStateUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for (var problem : nonExistent) {
      Log.error("State Usage "+node.getName()+ " has a redefinition/specialication \"" + printName(problem) + "\" ,could not find state definition with this name .");
    }

    var nonExistentType = node.streamSpecializations()
        .flatMap(ASTSpecialization::streamSuperTypes).filter(t -> t instanceof ASTSysMLTyping)
        .filter(t -> node.getEnclosingScope().resolveStateDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for (var problem : nonExistentType) {
      Log.error("State Usage "+node.getName()+ " has the type \"" + printName(problem) + "\" ,could not find state definition with this name .");
    }
  }
}
