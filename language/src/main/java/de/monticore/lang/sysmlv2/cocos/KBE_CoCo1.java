/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class KBE_CoCo1 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // "Outputs von Subkomponenten dürfen nur zu Inputs von Subkomponenten
    //  oder Outputs der Oberkomponente verbunden werden."
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    var src = node.getSrc();
    var tgt = node.getTgt();

        
  }
}
