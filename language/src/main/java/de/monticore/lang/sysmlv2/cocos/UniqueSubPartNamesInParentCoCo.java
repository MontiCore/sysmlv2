/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo: Jede Subkomponente muss einen eindeutigen Namen haben
 */
public class UniqueSubPartNamesInParentCoCo implements SysMLPartsASTPartUsageCoCo {

  @Override
  public void check(ASTPartUsage node) {

    var scope = node.getEnclosingScope();

    String partName = node.getName();

    int matches = scope
        .resolvePartUsageLocallyMany(false, partName,
            AccessModifier.ALL_INCLUSION, p -> true)
        .size();

    if (matches > 1) {
      Log.error(
          "0x10AA7 The subcomponent name used in 'def' \"" + partName
              + "\" is not unique in the model.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
