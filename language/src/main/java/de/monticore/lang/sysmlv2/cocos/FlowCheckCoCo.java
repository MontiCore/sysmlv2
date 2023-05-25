/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTFlowCoCo;
import de.se_rwth.commons.logging.Log;

public class FlowCheckCoCo implements SysMLConnectionsASTFlowCoCo {

  @Override
  public void check(ASTFlow node) {
    Log.warn("0xA70001 Make use of Connector instead of Flow Connections");
  }
}
