package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;

public class AttributeUsageSymbol extends AttributeUsageSymbolTOP {

  public AttributeUsageSymbol(String name) {
    super(name);
  }

  /**
   * Returns whether this is an input. Returns true for direction "inout", thus not mutually exclusive with
   * {@link #isOut()}
   */
  public boolean isIn() {
    return getDirection() == ASTSysMLFeatureDirection.IN || getDirection() == ASTSysMLFeatureDirection.INOUT;
  }

  /**
   * Returns whether this is an output. Returns true for direction "inout", thus not mutually exclusive with
   * {@link #isIn()}
   */
  public boolean isOut() {
    return getDirection() == ASTSysMLFeatureDirection.OUT || getDirection() == ASTSysMLFeatureDirection.INOUT;
  }

}
