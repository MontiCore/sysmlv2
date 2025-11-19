/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.se_rwth.commons.logging.Log;

public class KBE_CoCo5 implements SysMLPartsASTPartUsageCoCo {

  @Override
  public void check(ASTPartUsage node) {

    // Wenn kein Symbol vorhanden ist, ist der Part semantisch "kaputt":
    // dann ist z.B. der Typ nicht korrekt gebunden oder die Auflösung fehlgeschlagen.
    if (!node.isPresentSymbol()) {
      Log.error(
        "0xKBE05 Part '" + node.getName()
          + "' does not have a symbol; its referenced type may be missing or unresolved.",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
      return;
    }

    // Symbol ist vorhanden – hier könntest du später feiner prüfen,
    // z.B. ob das Symbol eine gültige Typreferenz hat.
    PartUsageSymbol sym = node.getSymbol();

  }
}
