/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;


/**
 * Checks that connected Ports can not be the same
 */
public class SamePortConnectionCoco implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // Skip validation if endpoints are missing
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }
    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();
    String srcName = src.getMCQualifiedName().toString();
    String tgtName = tgt.getMCQualifiedName().toString();

    if (srcName.equals(tgtName)){
      Log.error("0x10AA9 A port is connected to itself.",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd());
    }

  }

}
