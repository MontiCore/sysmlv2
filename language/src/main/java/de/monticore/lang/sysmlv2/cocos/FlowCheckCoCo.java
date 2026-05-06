/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTFlowUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTFlowUsageCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Flow-Konnectoren stellen ein Spezialfall der regulären Konnektoren dar.
 * Wir (SpesML v2) sehen es so vor, die Verwendung der allgemeinen Konnektoren
 * dem Flow-Konnectoren vorzuziehen.
 */
public class FlowCheckCoCo implements SysMLPartsASTFlowUsageCoCo {

  @Override
  public void check(ASTFlowUsage node) {
    Log.warn("0xFF0001 Make use of Connector instead of Flow Connections",
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }
}
