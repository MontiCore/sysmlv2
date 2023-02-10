package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTFlowCoCo;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;

public class ConnectionGeneratorCoCos implements SysMLConnectionsASTFlowCoCo {
  /**
   * Check that a flow type exists, that the directions fit and src/tgt exist
   */
  @Override public void check(ASTFlow node) {

    var astNode = node.getEnclosingScope().getAstNode();

    if(astNode instanceof ASTPartDef) {

    }
    //TODO check src and target exists

    //TODO directions are correct

  }

}
