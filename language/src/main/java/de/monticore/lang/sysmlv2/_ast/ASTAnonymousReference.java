package de.monticore.lang.sysmlv2._ast;

public class ASTAnonymousReference extends ASTAnonymousReferenceTOP {

  @Override
  public String getName() {
    return getSrc().getQName();
  }

  @Override
  public void setModifier(de.monticore.umlmodifier._ast.ASTModifier modifier) {
    if (modifier == null || modifier instanceof ASTModifier) {
      super.setModifier((ASTModifier) modifier);
    }
    else {
      de.se_rwth.commons.logging.Log.error("Expected SysMLv2 ASTModifier, but got "
          + modifier.getClass().getName());
    }
  }

}
