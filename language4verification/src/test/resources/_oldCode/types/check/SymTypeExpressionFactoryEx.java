package schrott.types.check;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

/**
 * The original factory does not provide methods to create function types.
 * They do note that function type creators will potentially be added in the future. Until then, we implement our own.
 */
public class SymTypeExpressionFactoryEx extends SymTypeExpressionFactory {

  /**
   * Creates a function type from a function symbol
   *
   * @param symbol function symbol
   * @return SymTypeOfFunction type of the function symbol
   */
  public static SymTypeExpression createTypeExpression(FunctionSymbol symbol) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(symbol.getName());
    typeSymbol.setEnclosingScope(symbol.getEnclosingScope());
    return new SymTypeOfFunction(typeSymbol, symbol);
  }
}
