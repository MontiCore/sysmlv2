package de.monticore.lang.sysmlv2._ast;

import de.se_rwth.commons.logging.Log;

public class ASTUserDefinedUsage extends ASTUserDefinedUsageTOP {

  /**
   * UserDefinedUsage implementiert ASTSysMLUsage, das die Methode setModifier mit
   * dem allgemeinen UML-Modifier-Typ deklariert. Die generierte TOP-Klasse
   * speichert jedoch den spezifischeren SysMLv2-Modifier-Typ. Dieser Override ist
   * daher nötig, um die Interface-Methode zu implementieren und kompatible
   * SysMLv2-Modifier an den generierten Setter weiterzuleiten.
   */

  @Override
  public void setModifier(de.monticore.umlmodifier._ast.ASTModifier modifier) {
    if (modifier == null || modifier instanceof ASTModifier) {
      super.setModifier((ASTModifier) modifier);
    }
    else {
      Log.error("Expected SysMLv2 ASTModifier, but got "
          + modifier.getClass().getName());
    }
  }
}
