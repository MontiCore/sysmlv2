/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * CoCo2: Jeder im "part def X refines Name" verwendete Name muss eine existierende Part-Definition sein.
 */
public class RefinementTargetDefinitionExistsCoCo implements SysMLPartsASTPartDefCoCo {

  protected String printPartType(ASTMCType type) {
    return type.printType();
  }

  @Override
  public void check(ASTPartDef node) {
    var nonExistent = node.streamSpecializations()
        .filter(s -> s instanceof ASTSysMLRefinement)
        .flatMap(ASTSpecialization::streamSuperTypes)
        .filter(t ->
            node.getEnclosingScope().resolvePartDef(printPartType(t)).isEmpty()
        )
        .collect(Collectors.toList());

    for (var problem : nonExistent) {
      Log.error(
          "0x10AA2 The name used in 'refines' \"" + printPartType(problem)
              + "\" does not exist as a part definition.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
