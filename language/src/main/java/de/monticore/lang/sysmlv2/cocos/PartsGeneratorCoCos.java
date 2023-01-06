package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class PartsGeneratorCoCos implements SysMLPartsASTPartUsageCoCo {

  /**
   * Check that at least one part def is extended.
   */
  @Override public void check(ASTPartUsage node) {
    var specialications = node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).collect(
        Collectors.toList());
    var relevantElements = node.getSysMLElementList().stream().filter(
        t -> t instanceof ASTActionDef | t instanceof ASTActionUsage | t instanceof ASTAttributeUsage
            | t instanceof ASTPartUsage).collect(
        Collectors.toList()).size(); //partUsage with at least one of the types is seen as a adhoc class definition
    if(specialications.isEmpty() && relevantElements == 0)
      Log.error("The Part Usage " + node.getName() + " has to extend at least one part def");
  }
}
