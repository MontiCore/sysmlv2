package de.monticore.lang.sysml.sysml._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AddImportToScopeVisitor implements SysMLInheritanceVisitor {
  //TODO check if current scope is global scope or
  //TODO better try to reference SysMLType => ASTPackage
  List<String> warnings = new ArrayList<>();
  ISysMLNamesBasisScope globalScope;
  public void startTraversal(ASTUnit ast, ISysMLNamesBasisScope globalScope) {
    this.globalScope = globalScope;
    ast.accept(this);

  }

  @Override
  public void visit(ASTAliasPackagedDefinitionMember node){
    outForTesting("Checking node");
    resolveImports(node.getQualifiedName(), node.getEnclosingScope().getEnclosingScope().getEnclosingScope(),
        node.getEnclosingScope(), false);
    //TODO remove
    /*if(!node.getEnclosingScope().resolveSysMLType(node.getQualifiedName().getReferencedName()).isPresent()){
      Log.error("Type was not added to scope correctly - this is just for debugging.");
    }*/
  }
  @Override
  public void visit(ASTImportUnitStd node) {
    outForTesting("Checking node");
    resolveImports(node.getQualifiedName(), node.getEnclosingScope().getEnclosingScope().getEnclosingScope(),
        node.getEnclosingScope(), node.isStar());
  }
  public void resolveImports(ASTQualifiedName qualifiedName, ISysMLNamesBasisScope scopeToSearchIn,
      ISysMLNamesBasisScope currentEnclosingScope, boolean starImport ){

    //Problem is ArtifactScope und GlobalScope und dann packageScope
    //TODO try to get Package (If it is package with SysMLType)
    outForTesting("Trying to resolve import " + qualifiedName.getFullQualifiedName());

    //resolving importUnit
    List<ISysMLNamesBasisScope> searchingScope = new ArrayList<>();
    searchingScope.addAll(globalScope.getSubScopes()); //TODO or scopeToSearchIn
    ISysMLNamesBasisScope scope = currentEnclosingScope;
    // First scope is package, but we want to have the package above.
    // TODO importUnit.getQualifiedName().ge
    List<String> names = qualifiedName.getNamesList();
    if(names.size()==0){
      Log.error("Internal error in " + this.getClass().getName() + ". A Qualified Name should always start with a "
          + "Name");
    }
    Boolean lastNameOfList = false;

    List<ISysMLNamesBasisScope> resolvedScope = new ArrayList<>();
    List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();
    for(int i= 0; i< names.size();i++) {
      String name = names.get(i);
      if(i ==names.size() -1 ){
        lastNameOfList = true; //Now it can also be a direct reference
      }
      List<ISysMLNamesBasisScope> currentSearchingScopes = new ArrayList<>();
      for (ISysMLNamesBasisScope s: searchingScope) {
        currentSearchingScopes.add(s);
      }
      searchingScope = new ArrayList<>();
      for (ISysMLNamesBasisScope searchHere : currentSearchingScopes) {
        searchingScope.addAll(resolveNameAsScope(name, searchHere));
      }
      if(lastNameOfList){
        resolvedScope = searchingScope;
        for(ISysMLNamesBasisScope searchHere : currentSearchingScopes){
          resolvedTypes = resolveNameAsSysMLType(name, searchHere);
        }
      }
    }

    if(resolvedScope.size() == 0 && resolvedTypes.size()==0){
      warnings.add("Could not resolve import. "); //TODO put in CoCo.
    }else if(resolvedTypes.size()==0 || (resolvedTypes.size()==1 &&resolvedScope.size()== 1)){
      //Importing a package.

      if(starImport){
        for (ISysMLNamesBasisScope importThis : resolvedScope) {
          LinkedListMultimap<String, SysMLTypeSymbol> imports = importThis.getSysMLTypeSymbols();
          for(SysMLTypeSymbol importSymbol:imports.values()){
            scope.add(importSymbol); //TODO check  if symbol is already in the current scope
          }
        }
      }else{
       warnings.add("Importing package without a star (e.g.\"::*\") will have no effect. "
            + "If this Statement imports something else then a scope, this has no effect.");//TODO CoCo
      }
    }else if(resolvedTypes.size()>1){
      warnings.add("The import statement was ambiguous, nothing will be imported. ");//TODO CoCo
    }else if(resolvedTypes.size() == 1 && resolvedScope.size() ==0){
      outForTesting("Adding symbol to scope : " + resolvedTypes.get(0).getName() );
      //TODO check if symbol is already in the scope.
      scope.add(resolvedTypes.get(0));
    }else{
      Log.error("Internal Error in " + AddImportToScopeVisitor.class.getName() + ". Unexpected case.");
    }

    for (String warning : warnings) {
      // Log.warn(warning, qualifiedName.get_SourcePositionStart()); TODO
    }
  }


  private List<ISysMLNamesBasisScope> resolveNameAsScope(String name, ISysMLNamesBasisScope top){
    List<ISysMLNamesBasisScope> res = new ArrayList<>();
    for (ISysMLNamesBasisScope currentScope: top.getSubScopes()) {
      outForTesting("Looking for scope with Name " + name + " current scope has name " + currentScope.getName());
      if(currentScope.isPresentName()){
        if(currentScope.getName().equals(name)){

          res.add(currentScope);
        }
      }
    }
    return res;
  }
  private List<SysMLTypeSymbol> resolveNameAsSysMLType(String name, ISysMLNamesBasisScope scope){
    outForTesting("Looking for SysMLSymbolType with Name " + name + " in scope " + scope.getName());
    List<SysMLTypeSymbol> res = scope.resolveSysMLTypeMany(name);
    outForTesting("Found " + res.size() + " matching symbols.");
    return res;
  }

  private void outForTesting(String s){ //TODO remove
    // System.out.println("-----------------------" + s);
  }
}
