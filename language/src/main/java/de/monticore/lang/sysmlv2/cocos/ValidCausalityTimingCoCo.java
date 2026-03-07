/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTSysMLCausality;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTSysMLCausalityCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Set;

public class ValidCausalityTimingCoCo implements SysMLPartsASTSysMLCausalityCoCo {
  protected static final Set<String> VALID_TIMINGS = Set.of("instant", "delayed");

  @Override
  public void check(ASTSysMLCausality node) {
    if (!VALID_TIMINGS.contains(node.getTiming())) {
      Log.error(
          "0x10AA8 Illegal timing value '" + node.getTiming()
              + "'. Only 'instant' and 'delayed' are allowed.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}