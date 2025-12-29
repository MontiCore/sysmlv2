/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlv2._ast.ASTPartDef;
import de.monticore.lang.sysmlv2._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/**
 * Eine sinnvoll modellierte Part sollte zur Außenweltkommunikation mindestens über ein
 * Port besitzen. Diese CoCo prüft, ob mind. ein Port innerhalb des Part-Scopes definiert wurde.
 */
public class PortDefinitionExistsCoCo implements SysMLPartsASTPartDefCoCo {
  @Override
  public void check(ASTPartDef node) {
    List<ASTPortUsage> portUsages = node.getSysMLElements(ASTPortUsage.class);
    if(portUsages.isEmpty()) {
      Log.warn("0xFF0002 Part " + node.getName() + " must use at least one port!",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
