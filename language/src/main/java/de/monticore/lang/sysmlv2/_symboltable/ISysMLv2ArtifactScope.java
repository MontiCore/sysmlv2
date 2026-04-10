package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.SetAsListAdapter;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Predicate;

public interface ISysMLv2ArtifactScope extends ISysMLv2ArtifactScopeTOP {

  @Override
  default List<TypeSymbol> continueTypeWithEnclosingScope(
    boolean foundSymbols,
    String name,
    AccessModifier modifier,
    Predicate<TypeSymbol> predicate
  ) {
    final LinkedHashSet<TypeSymbol> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosingScope(foundSymbols)
      && getEnclosingScope() != null) {

      if (!(getEnclosingScope() instanceof IGlobalScope)) {
        Log.warn("0xA1139 The artifact scope "
          + (isPresentName() ? getName() : "")
          + " should have a global scope as enclosing scope or no "
          + "enclosing scope at all.");
      }
      foundSymbols = foundSymbols | result.size() > 0;

      final Set<String> potentialQualifiedNames =
          calculateQualifiedNames(name, getPackageName(), getImportsList());

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        result.addAll(getEnclosingScope().resolveTypeMany(
          foundSymbols,
          potentialQualifiedName,
          modifier,
          predicate
        ));
      }

      /* FALLBACK: If standard resolution failed, append the ArtifactScope's
       * name as a namespace prefix.
       */
      if (result.isEmpty() && isPresentName() && !getName().isEmpty()) {
        final String prefixedName = getName() + "." + name;

        result.addAll(getEnclosingScope().resolveTypeMany(
          foundSymbols,
          prefixedName,
          modifier,
          predicate
        ));
      }

    }

    return new SetAsListAdapter(result);
  }
}
