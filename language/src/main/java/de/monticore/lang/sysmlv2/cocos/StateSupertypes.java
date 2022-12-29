/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

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
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveStateDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("Could not find state definition \"" + printName(problem) + "\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be state definitions or usages.
   */
  @Override
  public void check(ASTStateUsage node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveStateDef(printName(t)).isEmpty()
            && node.getEnclosingScope().resolveStateUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("Could not find state definition or usage with the name \"" + printName(problem) + "\".");
    }
  }
}
