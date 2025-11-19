/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * CoCo 4:
 * "Jeder im refines verwendete Part-Name muss definiert sein."
 *
 * Für alle Supertypen (Spezialisierungen) eines PartDef wird geprüft, ob es
 * entweder eine Part-Definition oder eine Part-Usage mit diesem Namen gibt.
 * Part-Usages selbst werden hier bewusst NICHT geprüft (das ist Aufgabe von CoCo5).
 */
public class KBE_CoCo4 implements SysMLPartsASTPartDefCoCo {

  @Override
  public void check(ASTPartDef node) {

    var nonExistent = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t ->
            node.getEnclosingScope().resolvePartDef(printName(t)).isEmpty()
                && node.getEnclosingScope().resolvePartUsage(printName(t)).isEmpty()
        )
        .collect(Collectors.toList());

    for (var problem : nonExistent) {
      Log.error(
          "0xKBE04 Could not find part definition or usage with the name \""
              + printName(problem)
              + "\" used in refines of part def \"" + node.getName() + "\".",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Gibt den Typnamen so zurück, wie er im Modell geschrieben wurde.
   * (Analog zu ItemsSupertypes / InterfaceSupertypes / ActionSupertypes)
   */
  protected String printName(ASTMCType t) {
    return t.toString();
  }
}
