package de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
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
    Boolean continueResolving = true;
    while (continueResolving){
      continueResolving = false;
      resolvedTypes = resolveQualifiedNameAsListInASpecificScope(qualifiedName.getNamesList(), scopeToSearchIn);

      //If we could not resolve anything go a scope higher
      if(resolvedTypes.size()==0){
        if(!(scopeToSearchIn instanceof SysMLGlobalScope)){ //Is there a higher scope?

          //Get higher scope
          scopeToSearchIn = scopeToSearchIn.getEnclosingScope();
          if (scopeToSearchIn instanceof SysMLArtifactScope) {
            //Artifact scope is not the scope to search in, but global scope.
            scopeToSearchIn = scopeToSearchIn.getEnclosingScope();
          }

          continueResolving = true;
        }
      }
    }

    return resolvedTypes;
  }

  /*
   * @param names A list of names to resolve starting with the highest scope. E.g. [PackageA,SubPackageB,blockXY]
   * @param scopeToSeachIn If this is called from a QualifiedName inside an SysML model, this should be
   *                       .getEnclosingScope() . If you want to resolve anything from global perspective, this should
   *                        be the SysMLGlobalScope of the model directory. (You can get the SysMLGlobalScope by
   *                        calling two time .getEnclosingScope() on any root element (ASTUnit) of a model.
   * @return a list of SysMLTypeSymbols. Even if the qualified name refers to a scope (e.g., a package for star
   *         import). This scope is always also a SysMLType.
   */
  public static List<SysMLTypeSymbol> resolveQualifiedNameAsListInASpecificScope(List<String> names,
      ISysMLNamesBasisScope scopeToSearchIn) {
    List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();

    List<ISysMLNamesBasisScope> searchingScope = new ArrayList<>();

    if (scopeToSearchIn instanceof SysMLArtifactScope) {
      //Artifact scope is not the scope to search in, but global scope.
      scopeToSearchIn = scopeToSearchIn.getEnclosingScope();
    }
    searchingScope.addAll(scopeToSearchIn.getSubScopes());
    if (names.size() == 0) {
      Log.error("Internal error in " + ResolveQualifiedNameHelper.class.getName() +
          ". The list of names (possibly belonging to a qualified name), should not have size 0.");
    }
    boolean lastNameOfList = false;

    for (int i = 0; i < names.size(); i++) {
      String name = names.get(i);
      if (i == names.size() - 1) {
        lastNameOfList = true; //Now it can also be a direct reference
      }
      List<ISysMLNamesBasisScope> currentSearchingScopes = new ArrayList<>();
      for (ISysMLNamesBasisScope s : searchingScope) {
        currentSearchingScopes.add(s);
      }
      searchingScope = new ArrayList<>(); //Reset SearchingScope
      if (lastNameOfList) { //If all the scopes are resolved, we are looking for a SysMLType.
        for (ISysMLNamesBasisScope searchHere : currentSearchingScopes) {
          resolvedTypes = resolveNameAsSysMLType(name, searchHere);
        }
      }
      else { //Else look to resolve the scopes.
        for (ISysMLNamesBasisScope searchHere : currentSearchingScopes) {
          searchingScope.addAll(resolveNameAsScope(name, searchHere));
        }
      }
    }
    return resolvedTypes;
  }

  private static List<ISysMLNamesBasisScope> resolveNameAsScope(String name, ISysMLNamesBasisScope top) {
    List<ISysMLNamesBasisScope> res = new ArrayList<>();
    for (ISysMLNamesBasisScope currentScope : top.getSubScopes()) {
      //outForTesting("Looking for scope with Name " + name + " current scope has name " + currentScope.getName());
      if (currentScope.isPresentName()) {
        if (currentScope.getName().equals(name)) {

          res.add(currentScope);
        }
      }
    }
    return res;
  }

  private static List<SysMLTypeSymbol> resolveNameAsSysMLType(String name, ISysMLNamesBasisScope scope) {
    //outForTesting("Looking for SysMLSymbolType with Name " + name + " in scope " + scope.getName());
    List<SysMLTypeSymbol> res = scope.resolveSysMLTypeMany(name);
    //outForTesting("Found " + res.size() + " matching symbols.");
    return res;
  }
}
