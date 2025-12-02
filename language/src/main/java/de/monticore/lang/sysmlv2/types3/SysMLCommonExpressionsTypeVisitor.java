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
import de.monticore.lang.sysmlactions._ast.ASTAssignmentActionUsage;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlexpressions._ast.ASTConditionalNotExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTInfinity;
import de.monticore.lang.sysmlexpressions._ast.ASTSubsetEquationExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSubsetExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSupersetEquationExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSupersetExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLFieldAccessExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLInstantiation;
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
import de.monticore.ocl.setexpressions._ast.ASTSetNotInExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

/**
 * Handles type checking the sysmlv2 standard and the particularities of the spes
 * language profile regarding streams in expressions.
 * 1. Ports in specifications have implicit stream types and are injected in the
 * type check at field access derivation and at name expression derivation.
 * Also implements the following design decisions using two modes:
 * Mode 1: TypeCheck interprets an Expression for a port usage as an implicit
 * field access if there is only one attribute inside. Explicit field accesses
 * are still supported by ignoring the field access if its type correct.
 * Mode 2: TypeCheck finds multiple attributes, and thus multiple channels and
 * reverts to the standard (with streams) TypeCheck without implicit field accesses.
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
  public void endVisit(ASTInfinity node) {
    // backwards compatibility
    getType4Ast().setTypeOfExpression(node, SymTypeExpressionFactory.createPrimitive("int"));
  }

  @Override
  public void endVisit(ASTSysMLFieldAccessExpression node) {
    // does not work with inheritance traverser because this is the handler and
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

      //++++++++++ modify start here ++++++++++

      Optional<PortSymbol> optPort = Optional.empty();
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

      var innerTypeIsPortDef =  innerAsExprType.hasTypeInfo() &&
          innerAsExprType
              .getTypeInfo()
              .getSpannedScope()
              .getSpanningSymbol() instanceof PortDefSymbol
          || innerAsExprType.isArrayType() &&
          innerAsExprType
              .asArrayType()
              .getArgument()
              .getTypeInfo()
              .getSpannedScope()
              .getSpanningSymbol() instanceof PortDefSymbol;

      // If no C&C port was found OR the inner expression refers to a port definition
      if (optPort.isEmpty() || innerTypeIsPortDef) {

        // Case 1: This is not an implicit field access, we still have to compute it.
        // Fall back to the default field access computation
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

          // We can only check if we are not in an automata as state machines always define a scope
          if (type.isPresent() &&
              !type.get().isFunctionType() &&
              !SysMLWithinScopeBasicSymbolResolver.isDefinedInStateMachine(
                  getAsBasicSymbolsScope(expr.getEnclosingScope())) &&
              optPort.isPresent()) {
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
        // Case 2: this is an explicit FA and the type of the inner expression is forwarded
        // to the outer expression as it was already computed in a previous step
        type = Optional.of(innerAsExprType);
      }
    }
    return type;
  }

  @Override
  public void endVisit(ASTSubsetExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                this::calculateSubsetExpression)
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSubsetEquationExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                this::calculateSubsetExpression)
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSupersetExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                this::calculateSubsetExpression)
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSupersetEquationExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                this::calculateSubsetExpression)
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateSubsetExpression(
      SymTypeExpression left, SymTypeExpression right) {

    if (SymTypeRelations.isCompatible(left, right) && MCCollectionSymTypeRelations.isSet(left)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return SymTypeExpressionFactory.createObscureType();
  }

  @Override
  public void endVisit(ASTSysMLInstantiation expr) {
    getType4Ast().setTypeOfExpression(expr, getType4Ast().getPartialTypeOfTypeId(
        expr.getMCType()));
  }
}
