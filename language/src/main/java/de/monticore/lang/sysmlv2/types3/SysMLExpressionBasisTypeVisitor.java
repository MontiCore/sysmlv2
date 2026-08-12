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

    if (enclosingScope instanceof ISysMLv2Scope) {
      ISysMLv2Scope sysmlScope = (ISysMLv2Scope) enclosingScope;
      Optional<TypeSymbol> typeSymbol = sysmlScope.resolveType(name);

      if (typeSymbol.isPresent()) {
        return Optional.of(SymTypeExpressionFactory.createTypeObject(typeSymbol.get()));
      }
    }

    return Optional.empty();
  }
}
