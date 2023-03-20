package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlparts._ast.ASTSysMLEnumConstant;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;

public class SysMLPartsScopesGenitor extends SysMLPartsScopesGenitorTOP {

  /**
   * Used to set enum literals to static
   */
  @Override
  protected void initFieldHP2(FieldSymbol symbol)
  {
    super.initFieldHP2(symbol);
    if(symbol.isPresentAstNode() && symbol.getAstNode() instanceof ASTSysMLEnumConstant) {
      symbol.setIsStatic(true);
    }
  }

}
