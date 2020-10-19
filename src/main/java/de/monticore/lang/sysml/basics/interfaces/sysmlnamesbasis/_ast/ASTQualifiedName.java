package de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageBuilder;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.bdd._ast.ASTBlockBuilder;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScopeBuilder;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public interface ASTQualifiedName extends ASTQualifiedNameTOP{
  public List<String> getNamesList();
  public String getFullQualifiedName();


  public List<SysMLTypeSymbol> resolveSymbols();

  static List<SysMLTypeSymbol> resolveSymbolsHelper(ASTQualifiedName qualifiedName){
    List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();

    List<ISysMLNamesBasisScope> searchingScope = new ArrayList<>();
    ISysMLNamesBasisScope scopeToSearchIn = qualifiedName.getEnclosingScope().getEnclosingScope();
    if (scopeToSearchIn instanceof SysMLArtifactScope) { //Artifact scope is not the scope to search in, but global scope.
      scopeToSearchIn = scopeToSearchIn.getEnclosingScope();
    }
    searchingScope.addAll(scopeToSearchIn.getSubScopes());
    List<String> names = qualifiedName.getNamesList();
    if (names.size() == 0) {
      Log.error("Internal error in " + qualifiedName.getClass().getName() +
          ". A Qualified Name should always start with a Name");
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
      searchingScope = new ArrayList<>();
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


  static List<ISysMLNamesBasisScope> resolveNameAsScope(String name, ISysMLNamesBasisScope top) {
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

  static List<SysMLTypeSymbol> resolveNameAsSysMLType(String name, ISysMLNamesBasisScope scope) {
    //outForTesting("Looking for SysMLSymbolType with Name " + name + " in scope " + scope.getName());
    List<SysMLTypeSymbol> res = scope.resolveSysMLTypeMany(name);
    //outForTesting("Found " + res.size() + " matching symbols.");
    return res;
  }
}
