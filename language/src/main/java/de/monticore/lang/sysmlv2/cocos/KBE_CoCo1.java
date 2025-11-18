/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlstructure._ast.ASTSysMLConnection;
import de.monticore.lang.sysmlstructure._cocos.SysMLStructureASTSysMLConnectionCoCo;
import de.monticore.lang.sysmlstructure._symboltable.*;
import de.monticore.lang.sysmlstructure._symboltable.PortDirection;
import de.se_rwth.commons.logging.Log;


public class KBE_CoCo1 implements SysMLStructureASTSysMLConnectionCoCo {

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