package schrott._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLAccessModifierPrivate;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ResolveQualifiedNameHelper {

  public static List<SysMLTypeSymbol> resolveSymbolsHelper(ASTQualifiedName qualifiedName) {
    ISysMLNamesBasisScope scopeToSearchIn = qualifiedName.getEnclosingScope();
    List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();
    boolean continueResolving = true;
    while (continueResolving) {
      continueResolving = false;
      resolvedTypes = resolveQualifiedName(qualifiedName.getNamesList(), scopeToSearchIn);

      //If we could not resolve anything go a scope higher
      if (resolvedTypes.size() == 0) {
        if (!(scopeToSearchIn instanceof ISysMLGlobalScope)) { //Is there a higher scope?

          //Get higher scope
          scopeToSearchIn = scopeToSearchIn.getEnclosingScope();
          if (scopeToSearchIn instanceof ISysMLArtifactScope) {
            //Artifact scope is not the scope to search in, but global scope.
            scopeToSearchIn = scopeToSearchIn.getEnclosingScope();
          }

          continueResolving = true;
        }
      }
    }

    return resolvedTypes;
  }

  /**
   * TODO Doc
   *
   * @param fqnAsList Fully qualified name that we are resolving (already split to list)
   * @param scope The scope used for resolution
   * @return
   */
  private static List<SysMLTypeSymbol> resolveQualifiedName(
      List<String> fqnAsList,
      ISysMLNamesBasisScope scope)
  {
    List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();

    List<ISysMLNamesBasisScope> searchingScope = new ArrayList<>();

    if (scope instanceof ISysMLArtifactScope) {
      //Artifact scope is not the scope to search in, but global scope.
      scope = scope.getEnclosingScope();
    }
    if (fqnAsList.size() > 1) { // Qualified Nmae
      searchingScope.addAll(scope.getSubScopes());
    }
    else if (fqnAsList.size() == 1) { // Just look in current scope.
      return scope.resolveSysMLTypeMany(fqnAsList.get(0));
    }
    else {
      Log.error("Internal error in " + ResolveQualifiedNameHelper.class.getName() +
          ". The list of fqnAsList (possibly belonging to a qualified name), should not have size 0.");
    }

    for (int i = 0; i < fqnAsList.size(); i++) {
      boolean firstName = true; //Still can reference private elements
      if (i != 0) {
        firstName = false;
      }
      String name = fqnAsList.get(i);
      List<ISysMLNamesBasisScope> currentSearchingScopes = new ArrayList<>(searchingScope);
      searchingScope = new ArrayList<>(); //Reset SearchingScope
      if (i == fqnAsList.size() - 1) { //If all the scopes are resolved, we are looking for a SysMLType.
        for (ISysMLNamesBasisScope currentScope : currentSearchingScopes) {
          resolvedTypes.addAll(resolveNameAsSysMLType(name, currentScope));
        }
      }
      else { //Else look to resolve the scopes.
        for (ISysMLNamesBasisScope searchHere : currentSearchingScopes) {
          searchingScope.addAll(resolveNameAsScope(name, searchHere, firstName));
          if (searchingScope.size() == 0) {
            //Could also be in an imported scope
            List<SysMLTypeSymbol> possibleImportedScopes = resolveNameAsSysMLType(name, searchHere);
            if (possibleImportedScopes.size() == 1) { //unique
              List<ISysMLNamesBasisScope> possibleScope = resolveNameAsScope(name,
                  possibleImportedScopes.get(0).getAstNode().getEnclosingScope(),
                  firstName);
              if (possibleScope.size() == 1) { //unique
                searchingScope.add(possibleScope.get(0));
              }
            }
          }
        }

      }
    }

    List<SysMLTypeSymbol> resultingSymbols = new ArrayList<>();

    if (fqnAsList.size() > 1) {//Remove all private symbols.
      for (SysMLTypeSymbol symbol : resolvedTypes) {
        if (!(symbol.getAccessModifier() instanceof SysMLAccessModifierPrivate)) {
          resultingSymbols.add(symbol);
        }
      }
    }
    else { //Do not delete any symbols.
      resultingSymbols = resolvedTypes;
    }
    return resultingSymbols;
  }

  private static List<ISysMLNamesBasisScope> resolveNameAsScope(
      String name,
      ISysMLNamesBasisScope top,
      boolean firstName)
  {
    List<ISysMLNamesBasisScope> res = new ArrayList<>();
    List<ISysMLNamesBasisScope> scopesToSearchIn = new ArrayList<>();
    scopesToSearchIn.add(top);
    scopesToSearchIn.addAll(top.getSubScopes());
    for (ISysMLNamesBasisScope currentScope : scopesToSearchIn) {
      if (currentScope.isPresentName()) {
        if (currentScope.getName().equals(name)) {
          /*if(!firstName && resolveNameAsSysMLType(name, top).size()==1){ // Do not add private scopes.
            SysMLTypeSymbol belongingSymbol = resolveNameAsSysMLType(name, top).get(0);
            if(!(belongingSymbol.getAccessModifier() instanceof SysMLTypeSymbol)){
              res.add(currentScope);
            }
          }else{
            res.add(currentScope);
          }*/
          res.add(currentScope); // The above check is also not done in the current implementation, so it would be
          // only a guess how qualified names should behave in SysML. However, we can comment this check back in.
        }
      }
    }
    return res;
  }

  private static List<SysMLTypeSymbol> resolveNameAsSysMLType(String name, ISysMLNamesBasisScope scope) {
    // fixes import resolution bug.
    List<SysMLTypeSymbol> allSymbolsInScope = new ArrayList<>();
    if (scope instanceof ISysML4VerificationScope) {
      allSymbolsInScope.addAll(((ISysML4VerificationScope) scope).getBlockSymbols().values());
      allSymbolsInScope.addAll(((ISysML4VerificationScope) scope).getPortDefinitionStdSymbols().values());
      allSymbolsInScope.addAll(((ISysML4VerificationScope) scope).getStateDefinitionSymbols().values());
    }

    allSymbolsInScope.addAll(scope.getSysMLTypeSymbols().values());
    List<SysMLTypeSymbol> symbolsWithEqualName = new ArrayList<>();
    for (SysMLTypeSymbol s : allSymbolsInScope) {
      if (s.getName().equals(name)) {
        symbolsWithEqualName.add(s);
      }
    }
    return symbolsWithEqualName;
  }
}
