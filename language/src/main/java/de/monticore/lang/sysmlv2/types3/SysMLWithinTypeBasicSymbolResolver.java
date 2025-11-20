package de.monticore.lang.sysmlv2.types3;

import de.monticore.lang.componentconnector.StreamTimingUtil;
import de.monticore.lang.sysmlparts._symboltable.PortDefSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SysMLWithinTypeBasicSymbolResolver extends
    WithinTypeBasicSymbolsResolver {
  protected Optional<SymTypeExpression> _resolveVariable(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate) {
    Optional<SymTypeExpression> resolvedSymType;
    Optional<IBasicSymbolsScope> spannedScopeOpt = getSpannedScope(thisType);
    if (spannedScopeOpt.isEmpty()) {
      resolvedSymType = Optional.empty();
    }
    // search in this scope
    else {
      Optional<VariableSymbol> resolvedSymbolOpt = resolveVariableLocally(
          spannedScopeOpt.get(),
          name,
          accessModifier,
          predicate
      );
      if (resolvedSymbolOpt.isPresent()) {
        VariableSymbol resolvedSymbol = resolvedSymbolOpt.get();
        SymTypeExpression varType = replaceVariablesIfNecessary(
            thisType, resolvedSymbol.getType()
        );
        varType.getSourceInfo().setSourceSymbol(resolvedSymbol);
        resolvedSymType = Optional.of(varType);
      }
      else {
        resolvedSymType = Optional.empty();
      }
    }
    // search in super types
    if (resolvedSymType.isEmpty()) {
      // private -> protected while searching in super types
      AccessModifier superModifier = private2Protected(accessModifier);
      List<SymTypeExpression> superTypes = getSuperTypes(thisType);
      resolvedSymType = Optional.empty();
      for (SymTypeExpression superType : superTypes) {
        Optional<SymTypeExpression> resolvedInSuper =
            resolveVariable(superType, name, superModifier, predicate);
        if (resolvedSymType.isPresent() && resolvedInSuper.isPresent()) {
          Log.error("0xFD222 found variables with name \""
              + name + "\" in multiple super types of \""
              + thisType.printFullName() + "\"");
        }
        else if (resolvedSymType.isEmpty() && resolvedInSuper.isPresent()) {
          resolvedSymType = resolvedInSuper;
        }
        //filter based on local variables
      }
    }

    // not expecting free type variables for fields,
    // e.g., class C { <T> T tVar; }
    if (resolvedSymType.isPresent()) {
      SymTypeExpression type = resolvedSymType.get();
      if (!replaceFreeTypeVariables(thisType, type).deepEquals(type)) {
        Log.error("0xFD228 unexpected free type variable in variable "
            + name + " resolved in " + thisType.printFullName() + ": "
            + type.printFullName()
        );
      }
    }

    return resolvedSymType;
  }
}
