package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * SysML-spezifische Erweiterung des Typechecks für NameExpressions, einer
 * Produktion aus der ExpressionBasis.mc4-Grammatik
 */
public class SysMLExpressionBasisTypeVisitor extends ExpressionBasisTypeVisitor {

  @Override
  protected Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression expr) {

    if (expr.getEnclosingScope() == null) {
      Log.error("0x10AAA  internal error: "
              + "enclosing scope of expression expected",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return Optional.empty();
    }

    final String name = expr.getName();
    IBasicSymbolsScope enclosingScope = getAsBasicSymbolsScope(expr.getEnclosingScope());

    Optional<SymTypeExpression> varResult =
        WithinScopeBasicSymbolsResolver.resolveNameAsExpr(enclosingScope, name);

    if (varResult.isPresent()) {
      return varResult;
    }


    // Dieser If-Block wurde zum vordefinierten calculateNameExpression hinzugefügt
    if (enclosingScope instanceof ISysMLv2Scope) {
      Optional<TypeSymbol> typeSymbol = enclosingScope.resolveType(name);

      if (typeSymbol.isPresent()) {
        return Optional.of(
          SymTypeExpressionFactory.createTypeObject(typeSymbol.get())
        );
      }
    }

    return Optional.empty();
  }
}
