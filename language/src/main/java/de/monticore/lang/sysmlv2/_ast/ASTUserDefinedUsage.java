package de.monticore.lang.sysmlv2._ast;

import de.se_rwth.commons.logging.Log;

public class ASTUserDefinedUsage extends ASTUserDefinedUsageTOP {

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
