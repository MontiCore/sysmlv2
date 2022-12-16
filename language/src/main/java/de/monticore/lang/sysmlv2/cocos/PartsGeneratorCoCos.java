package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class PartsGeneratorCoCos implements SysMLPartsASTPartUsageCoCo {

  /**
   * Check that at least one part def is extende.
   */
  @Override public void check(ASTPartUsage node) {
    var specialications = node.streamSpecializations().flatMap(s -> s.streamSuperTypes()).collect(Collectors.toList());

    if(specialications.isEmpty())
      Log.error("A Part Usage has to extend at least one part def");
  }
}
