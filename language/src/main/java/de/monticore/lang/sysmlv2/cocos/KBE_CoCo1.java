/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTDecideAction;
import de.monticore.lang.sysmlactions._ast.ASTForkAction;
import de.monticore.lang.sysmlactions._ast.ASTJoinAction;
import de.monticore.lang.sysmlactions._ast.ASTLoopActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTMergeAction;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirstSuccession;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.se_rwth.commons.logging.Log;


public class KBE_CoCo1 implements SysMLConnectionsASTInterfaceUsageCoCo {

  @Override
  public void check(ASTSysMLConnection node) {
    ISysMLStructureScope scope = node.getEnclosingScope();

    var leftPortOpt =
      scope.resolvePortUsage(node.getLeft().getQName());
    var rightPortOpt =
      scope.resolvePortUsage(node.getRight().getQName());

    if(!leftPortOpt.isPresent()) {
      Log.error("Left port '" + node.getLeft().getQName()
        + "' cannot be resolved.", node.get_SourcePositionStart());
      return;
    }

    if(!rightPortOpt.isPresent()) {
      Log.error("Right port '" + node.getRight().getQName()
        + "' cannot be resolved.", node.get_SourcePositionStart());
      return;
    }

    var leftPort = leftPortOpt.get();
    var rightPort = rightPortOpt.get();

    PortDirection leftDir = leftPort.getDirection();
    PortDirection rightDir = rightPort.getDirection();

    boolean allowed =
        (leftDir == PortDirection.OUT && rightDir == PortDirection.IN) ||
        (leftDir == PortDirection.OUT && rightDir == PortDirection.OUT_PARENT) ||
        (leftDir == PortDirection.IN_PARENT && rightDir == PortDirection.IN);

    if(!allowed) {
      Log.error("0xC0C03 Illegal port connection: "
          + leftPort.getName() + " (" + leftDir + ") -> "
          + rightPort.getName() + " (" + rightDir + "). "
          + "Allowed: Sub.Output → Sub.Input or Sub.Output → Parent.Output.",
          node.get_SourcePositionStart());
    }
  }
}