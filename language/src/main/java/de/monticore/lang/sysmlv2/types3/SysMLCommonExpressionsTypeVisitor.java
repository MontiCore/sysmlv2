package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._symboltable.ICommonExpressionsScope;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.componentconnector.StreamTimingUtil;
import de.monticore.lang.componentconnector._symboltable.IComponentConnectorScope;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLFieldAccessExpression;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortDefSymbol;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._symboltable.StateDefSymbol;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/*
  Handles the particularities of the sysml regarding streams in expressions.
  Ports in specifications have implicit stream types and the type check is not aware.
 */
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

        //++++++++++ modify start here ++++++++++
        if (type.isPresent() &&
            !type.get().isFunctionType() &&
            !isDefinedInStateMachine(expr.getEnclosingScope())) {
          // We can only identify that we are not in an automata as state machines always define a scope

          var optPort = ((IComponentConnectorScope) expr.getEnclosingScope()).resolvePort(
              SysMLv2Mill.prettyPrint(expr, false));

          if (optPort.isPresent()) {
            // found a C&C port -> Adjust type to Stream
            var streamType = WithinScopeBasicSymbolsResolver.resolveType(
                getAsBasicSymbolsScope(expr.getEnclosingScope()),
                StreamTimingUtil.mapTimingToStreamType(
                    optPort.get().getTiming()));

            if (streamType.isEmpty()) {
              Log.error("tried to resolve \"" + name + "\""
                      + " given expression of type "
                      + innerAsExprType.printFullName()
                      + " but no stream symbol could be resolved.",
                  expr.get_SourcePositionStart(),
                  expr.get_SourcePositionEnd()
              );
            }
            else {
              streamType.get().asGenericType().setArgument(0, type.get());
            }

            type = streamType;
          }
        }
        //++++++++++ modify end here ++++++++++

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

  /*
    This method assumes that a state machine (state def/usage) always defines a scope.
   */
  private boolean isDefinedInStateMachine(ICommonExpressionsScope scope) {
    var enclosingScope = (ISysMLv2Scope) scope;
    if (enclosingScope.getAstNode() instanceof ASTStateDef ||
        enclosingScope.getAstNode() instanceof ASTStateUsage
    ) {
      return true;
    }
    else if (enclosingScope.getAstNode() instanceof ASTPartDef ||
        enclosingScope.getAstNode() instanceof ASTPartUsage ||
        enclosingScope instanceof ISysMLv2ArtifactScope ||
        enclosingScope instanceof ISysMLv2GlobalScope
    ) {
      return false;
    }
    else
      return isDefinedInStateMachine(scope.getEnclosingScope());
  }
}
