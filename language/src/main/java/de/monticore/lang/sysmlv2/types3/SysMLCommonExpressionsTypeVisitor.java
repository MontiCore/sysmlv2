package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.commonexpressions._ast.ASTArrayAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpressionBuilder;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.commonexpressions._symboltable.ICommonExpressionsScope;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.componentconnector.StreamTimingUtil;
import de.monticore.lang.componentconnector._symboltable.IComponentConnectorScope;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlexpressions._ast.ASTConditionalNotExpression;
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
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

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
  public void endVisit(ASTConditionalNotExpression expr) {
    SymTypeExpression inner =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        "0xB0165", expr, "not",
        SysMLTypeVisitorOperatorCalculator.conditionalNot(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
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

      Optional<PortSymbol> optPort = Optional.empty();

      // TODO what if its an array index. Resolve for the right port.
      if (expr.getExpression() instanceof ASTArrayAccessExpression) {
        optPort = ((IComponentConnectorScope) expr.getEnclosingScope()).resolvePort(SysMLv2Mill.prettyPrint(
            new ASTFieldAccessExpressionBuilder()
                .setExpression(((ASTArrayAccessExpression)expr.getExpression()).getExpression())
                .setName(name)
                .build(),
            false));
      } else if (expr.getExpression() instanceof ASTNameExpression) {
        optPort = ((IComponentConnectorScope) expr.getEnclosingScope()).resolvePort(SysMLv2Mill.prettyPrint(expr, false));
      }

      // part 1 is ignoring FA for port // part 2 is fallback. ie inner type is a port usage thus type is a port def
      if (optPort.isEmpty()
          || innerAsExprType.isArrayType() && innerAsExprType.asArrayType().getArgument().getTypeInfo().getSpannedScope().getSpanningSymbol() instanceof PortDefSymbol
          || innerAsExprType.hasTypeInfo() && innerAsExprType.getTypeInfo().getSpannedScope().getSpanningSymbol() instanceof PortDefSymbol) //|| optPortDef.isPresent() && optPortDef.get().getInputAttributes().size() != 1) {
      {
        if (WithinTypeBasicSymbolsResolver.canResolveIn(innerAsExprType)) {
          AccessModifier modifier = innerAsExprType.hasTypeInfo() ?
              TypeContextCalculator.getAccessModifier(
                  innerAsExprType.getTypeInfo(), expr.getEnclosingScope()
              ) :
              AccessModifier.ALL_INCLUSION;

          type = resolveVariablesAndFunctionsWithinType(
              innerAsExprType,
              name,
              modifier,
              v -> true,
              f -> true
          );

        /*
        if (type.isEmpty()) {
          // are we looking for an implicit field access?
          // try to find the implicit type
          var attr = ((ISysMLv2Scope) innerAsExprType.getTypeInfo().getSpannedScope()).getLocalAttributeUsageSymbols();
          if (attr.size() <= 1) {
            type = resolveVariablesAndFunctionsWithinType(
                innerAsExprType,
                attr.get(0).getName(),
                modifier,
                v -> true,
                f -> true
            );
            // real type found. Now you need to reset the inner type
            // TODO can you set it correctly from the start?

            getType4Ast().setTypeOfExpression(expr.getExpression(), type.get());
          }
        }
*/
/*
        if (type.isEmpty() && innerAsExprType.getTypeInfo().getSpannedScope().getSpanningSymbol() instanceof PortDefSymbol) {
          // might be an implicit field access

          // 1. check if there is only one accessible attribute
          // TODO maybe use getallvariables?
          var attr = ((ISysMLv2Scope) innerAsExprType.getTypeInfo().getSpannedScope()).getLocalAttributeUsageSymbols();
          // TODO what if <1
          if (attr.size() == 1) {
            // 2. find its type, resolve methods
            var actualType = WithinTypeBasicSymbolsResolver.resolveVariable(
                innerAsExprType,
                attr.get(0).getName(),
                modifier,
                v -> true);

            if (!isDefinedInStateMachine(expr.getEnclosingScope())) {
              var streamType = WithinScopeBasicSymbolsResolver.resolveType(
                  innerAsExprType.getTypeInfo().getEnclosingScope(),
                  "EventStream");
              //StreamTimingUtil.mapTimingToStreamType(
              //  optPort.get().getTiming()));

              if (streamType.isEmpty()) {
                //Log.error("tried to resolve \"" + name + "\""
                //        + " given expression of type "
                //        + innerAsExprType.printFullName()
                //        + " but no stream symbol could be resolved.",
                //    expr.get_SourcePositionStart(),
                //    expr.get_SourcePositionEnd()
                //);
              }
              else {
                streamType.get().asGenericType().setArgument(0,
                    actualType.get());
              }

              return resolveVariablesAndFunctionsWithinType(streamType.get(),
                  name, modifier, v -> true, f -> true);
            }
          }
        }
*/
        //++++++++++ modify start here ++++++++++
        if (type.isPresent() &&
            !type.get().isFunctionType() &&
            !isDefinedInStateMachine(expr.getEnclosingScope())) {
          // We can only identify that we are not in an automata as state machines always define a scope

          // TODO now you resolve for what
          //var optPort = ((IComponentConnectorScope) expr.getEnclosingScope()).resolvePort(
            //  SysMLv2Mill.prettyPrint(expr, false));

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
          else {
            // it is not a C&C port
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
      else {
        // TODO invert
        type = Optional.of(innerAsExprType);
      }
    }
    return type;
  }

  @Override
  protected Optional<SymTypeExpression> resolveVariablesAndFunctionsWithinType(
      SymTypeExpression innerAsExprType,
      String name,
      AccessModifier modifier,
      Predicate<VariableSymbol> varPredicate,
      Predicate<FunctionSymbol> funcPredicate
  ) {
    Set<SymTypeExpression> types = new HashSet<>();
    Optional<SymTypeExpression> variable =
        WithinTypeBasicSymbolsResolver.resolveVariable(innerAsExprType,
            name,
            modifier,
            varPredicate
        );
    if (variable.isPresent()) {
      types.add(variable.get());
    }

    Collection<SymTypeOfFunction> functions =
        WithinTypeBasicSymbolsResolver.resolveFunctions(
            innerAsExprType,
            name,
            modifier,
            funcPredicate
        );
    types.addAll(functions);

    /*
    if (types.isEmpty() && innerAsExprType.getTypeInfo().getSpannedScope().getSpanningSymbol() instanceof PortDefSymbol) {
      // might be an implicit field access

      // 1. check if there is only one accessible attribute
      // TODO maybe use getallvariables?
      var attr = ((ISysMLv2Scope) innerAsExprType.getTypeInfo().getSpannedScope()).getLocalAttributeUsageSymbols();
      // TODO what if <1
      if (attr.size() == 1) {
        // 2. find its type, resolve methods
        var actualType = WithinTypeBasicSymbolsResolver.resolveVariable(
            innerAsExprType,
            attr.get(0).getName(),
            modifier,
            varPredicate);


        var streamType = WithinScopeBasicSymbolsResolver.resolveType(
            innerAsExprType.getTypeInfo().getEnclosingScope(), "EventStream");
        //StreamTimingUtil.mapTimingToStreamType(
        //  optPort.get().getTiming()));

        if (streamType.isEmpty()) {
          //Log.error("tried to resolve \"" + name + "\""
          //        + " given expression of type "
          //        + innerAsExprType.printFullName()
          //        + " but no stream symbol could be resolved.",
          //    expr.get_SourcePositionStart(),
          //    expr.get_SourcePositionEnd()
          //);
        }
        else {
          streamType.get().asGenericType().setArgument(0, actualType.get());
        }

        return resolveVariablesAndFunctionsWithinType(streamType.get(), name, modifier, varPredicate, funcPredicate);
      }
    }
*/
    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
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
