package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.commonexpressions._symboltable.ICommonExpressionsScope;
import de.monticore.lang.componentconnector.StreamTimingUtil;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._symboltable.PortDefSymbol;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeSourceInfo;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implements the normal and implicit field access modes for the TypeCheck.
 * We determine the type of the NameExpression according to if it is a port usage,
 * how many attributes it has and if it is a stream type or not.
 */
public class SysMLWithinScopeBasicSymbolResolver extends
    WithinScopeBasicSymbolsResolver {

  public static void init() {
    // TODO init might be wrong
    WithinScopeBasicSymbolsResolver.setDelegate(new SysMLWithinScopeBasicSymbolResolver());
  }

  @Override
  protected Optional<SymTypeExpression> _resolveNameAsExpr(
      IBasicSymbolsScope enclosingScope, String name) {
    Log.errorIfNull(enclosingScope);
    Log.errorIfNull(name);
    // collect all (potential) types
    Set<SymTypeExpression> types = new HashSet<>();

    // to circumvent current shortcomings in our resolver,
    // we resolve with the resolver AND resolve with the within type resolver
    // afterwards we evaluate which result to use

    // not necessarily in an enclosing type
    Optional<SymTypeExpression> optVar =
        resolveVariableWithoutSuperTypes(enclosingScope, name);

    //++++++++++ modify start here ++++++++++

    if (optVar.isPresent() && enclosingScope instanceof ISysMLv2Scope &&
        (optVar.get().hasTypeInfo() ||
        optVar.get().isArrayType() && optVar.get().asArrayType().getArgument().hasTypeInfo())
    ) {
      // found var.
      IBasicSymbolsScope scope;
      var typeToLookIn = optVar.get();
      if (optVar.get().isArrayType()) {
        scope = typeToLookIn.asArrayType().getArgument().getTypeInfo().getSpannedScope();
        typeToLookIn = typeToLookIn.asArrayType().getArgument();
      }
      else {
        scope = optVar.get().getTypeInfo().getSpannedScope();
      }

      var attr = ((ISysMLv2Scope) scope).getLocalAttributeUsageSymbols();
      if (attr.size() == 1 && scope.getSpanningSymbol() instanceof PortDefSymbol) {
        // its an implicit field access representing a port
        var actualType = WithinTypeBasicSymbolsResolver.resolveVariable(
            typeToLookIn, attr.get(0).getName(), AccessModifier.ALL_INCLUSION,
            v -> true);

        if (!isDefinedInStateMachine(enclosingScope)) {
          // We are in stream mode

          // We have to resolve for port to determine its timing
          var optPort = ((ISysMLv2Scope) enclosingScope).resolvePort(name + "." + attr.get(0).getName());

          var streamType = WithinScopeBasicSymbolsResolver.resolveType(
              enclosingScope,
              StreamTimingUtil.mapTimingToStreamType(
                  optPort.get().getTiming()));

          if (streamType.isEmpty()) {
            Log.error("0xFDA26 internal error: " +
                "cannot resolve stream symbol for " + name + ". Were stream symbols loaded? "
            );
          } else {
            if (actualType.get().isArrayType()) {
              streamType.get().asGenericType().setArgument(0,
                  actualType.get().asArrayType().getArgument());
              actualType.get().asArrayType().setArgument(streamType.get());
            } else {
              streamType.get().asGenericType().setArgument(0,
                  actualType.get());

              actualType = streamType;
            }
          }
        }
          // if present
        if (optVar.get().isArrayType()) {
          optVar.get().asArrayType().setArgument(actualType.get());
        }
        else {
          optVar = actualType;
        }
      }
    }

    //++++++++++ modify end here ++++++++++

    List<SymTypeOfFunction> funcs =
        resolveFunctionsWithoutSuperTypes(enclosingScope, name);
    // within type
    Optional<TypeSymbol> enclosingType =
        TypeContextCalculator.getEnclosingType(enclosingScope);
    Optional<SymTypeExpression> varInType = Optional.empty();
    List<SymTypeOfFunction> funcsInType = Collections.emptyList();
    if (enclosingType.isPresent()) {
      SymTypeExpression enclosingTypeExpr =
          SymTypeExpressionFactory.createFromSymbol(enclosingType.get());
      AccessModifier modifier = TypeContextCalculator
          .getAccessModifier(enclosingType.get(), enclosingScope);
      varInType = WithinTypeBasicSymbolsResolver.resolveVariable(
          enclosingTypeExpr, name, modifier, getVariablePredicate());
      funcsInType = WithinTypeBasicSymbolsResolver.resolveFunctions(
          enclosingTypeExpr, name, modifier, getFunctionPredicate());
    }
    // get the correct variable
    if (varInType.isPresent() && optVar.isPresent()) {
      SymTypeSourceInfo varInTypeInfo = varInType.get().getSourceInfo();
      SymTypeSourceInfo optVarInfo = optVar.get().getSourceInfo();
      if (varInTypeInfo.getSourceSymbol().isPresent() &&
          optVarInfo.getSourceSymbol().isPresent()
      ) {
        ISymbol varInTypeVarSymbol = varInTypeInfo.getSourceSymbol().get();
        ISymbol optVarVarSymbol = optVarInfo.getSourceSymbol().get();
        if (optVarVarSymbol.getEnclosingScope()
            .isProperSubScopeOf(varInTypeVarSymbol.getEnclosingScope())
        ) {
          types.add(optVar.get());
        }
        else {
          types.add(varInType.get());
        }
      }
      else {
        Log.error("0xFDA25 internal error: " +
            "expected variable symbol for resolved variable " + name
        );
      }
    }
    else if (varInType.isPresent()) {
      types.add(varInType.get());
    }
    else if (optVar.isPresent()) {
      types.add(optVar.get());
    }
    // get the correct functions
    // heuristic, as we assume the resolver to be extended in the near future,
    // and the correct solution would take longer to implement,
    // in Javalight, these cases may never happen anyways
    types.addAll(funcsInType);
    for (SymTypeOfFunction func : funcs) {
      if (funcsInType.stream().noneMatch(f -> f.getSymbol() == func.getSymbol())) {
        types.add(func);
      }
    }

    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      // this can be extended to mark the intersection as
      // an intersection that one has to select a type of.
      // The current interpretation is, that the result is all the possible types,
      // e.g. "a.b" could have the types of (int, int->int, boolean->int).
      // in Java, this would be filtered out earlier,
      // however, we support more (e.g. SymTypeOfFunction).
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
  }

  private boolean isDefinedInStateMachine(IBasicSymbolsScope scope) {
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
