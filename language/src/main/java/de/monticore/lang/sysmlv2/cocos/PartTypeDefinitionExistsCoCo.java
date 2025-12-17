/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * CoCo1: Jeder in "part name:Typ" verwendete Typ muss eine existierende Part-Definition sein.
 */
public class PartTypeDefinitionExistsCoCo implements SysMLPartsASTPartUsageCoCo {

  protected String printPartType(ASTMCType type) {
    return type.printType();
  }

  @Override
  public void check(ASTPartUsage node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(ASTSpecialization::streamSuperTypes)
        .filter(t ->
            node.getEnclosingScope().resolvePartDef(printPartType(t)).isEmpty()
        )
        .collect(Collectors.toList());

    for (var problem : nonExistent) {
      Log.error(
          "0x10AA1 The type referenced in a PartUsage \"" + printPartType(problem)
              + "\" does not exist as a part definition.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
