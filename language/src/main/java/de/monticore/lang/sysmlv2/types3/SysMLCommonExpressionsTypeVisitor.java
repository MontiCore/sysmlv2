package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLFieldAccessExpression;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.lang.sysmlparts._symboltable.PortDefSymbol;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SysMLCommonExpressionsTypeVisitor extends CommonExpressionsTypeVisitor implements
    SysMLExpressionsVisitor2, SysMLExpressionsHandler {

  protected SysMLExpressionsTraverser traverser;

  @Override
  public SysMLExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SysMLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void endVisit(ASTSysMLFieldAccessExpression node) {
    endVisit((ASTFieldAccessExpression) node);
  }

  @Override
  public void traverse(ASTSysMLFieldAccessExpression node) {
    traverse((ASTFieldAccessExpression) node);
  }

  @Override
  protected Optional<SymTypeExpression> calculateExprFieldAccess(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    Optional<SymTypeExpression> type;
    final String name = expr.getName();
    if (!getType4Ast().hasTypeOfExpression(expr.getExpression())) {
      Log.error("0xFD231 internal error:"
              + "unable to find type identifier for field access",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      type = Optional.empty();
    }
    else {
      SymTypeExpression innerAsExprType =
          getType4Ast().getPartialTypeOfExpr(expr.getExpression());
      if (WithinTypeBasicSymbolsResolver.canResolveIn(innerAsExprType)) {
        AccessModifier modifier = innerAsExprType.hasTypeInfo() ?
            TypeContextCalculator.getAccessModifier(
                innerAsExprType.getTypeInfo(), expr.getEnclosingScope()
            ) : AccessModifier.ALL_INCLUSION;


        type = resolveVariablesAndFunctionsWithinType(
            innerAsExprType,
            name,
            modifier,
            v -> true,
            f -> true
        );

        // TODO should i check for first scope spanning symbol?
        if (type.isPresent() &&
            innerAsExprType.getTypeInfo().getSpannedScope().getSpanningSymbol() instanceof PortDefSymbol &&
            //!(expr.getEnclosingScope().getSpanningSymbol() instanceof StateDefSymbol ||
            //expr.getEnclosingScope().getSpanningSymbol() instanceof StateUsageSymbol)) {
            !(expr.getEnclosingScope().getAstNode() instanceof ASTStateDef ||
            expr.getEnclosingScope().getAstNode() instanceof ASTStateUsage)) {
          // TODO error handling if Stream not found
          // works if the ast of the portdef is not there. BUT. Constraints and states are units so AST is accessible
          type = Optional.of(SymTypeExpressionFactory.createGenerics(
              ((IBasicSymbolsScope)expr.getEnclosingScope()).resolveType("EventStream").get(),
              type.get()
          ));
        }

        // Log remark about access modifier,
        // if access modifier is the reason it has not been resolved
        if (type.isEmpty() && !resultsAreOptional) {
          Optional<SymTypeExpression> potentialResult =
              resolveVariablesAndFunctionsWithinType(
                  innerAsExprType,
                  name,
                  AccessModifier.ALL_INCLUSION,
                  v -> true,
                  f -> true
              );
          if (potentialResult.isPresent()) {
            Log.warn("tried to resolve \"" + name + "\""
                    + " given expression of type "
                    + innerAsExprType.printFullName()
                    + " and symbols have been found"
                    + ", but due to the access modifiers (e.g., public)"
                    + ", nothing could be resolved",
                expr.get_SourcePositionStart(),
                expr.get_SourcePositionEnd()
            );
          }
        }
      }
      // extension point
      else {
        Log.error("0xFDB3A unexpected field access \""
                + expr.getName()
                + "\" for type "
                + innerAsExprType.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        type = Optional.empty();
      }
    }
    return type;
  }
}
