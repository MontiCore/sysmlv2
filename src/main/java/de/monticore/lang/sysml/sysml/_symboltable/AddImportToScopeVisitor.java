package de.monticore.lang.sysml.sysml._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AddImportToScopeVisitor implements SysMLInheritanceVisitor {
  int phase = 0;

  public void memorizeImportsPhase1of2(ASTUnit ast) {
    this.phase = 1;
    ast.accept(this);
  }

  public void addImportsToScopePhase2of2(ASTUnit ast) {
    this.phase = 2;
    ast.accept(this);
  }

  @Override
  public void visit(ASTAliasPackagedDefinitionMember node) {

    if (phase == 1) {
      if (node.isAlias() && node.getQualifiedName().getNamesList().size() == 1) { //Just renaming of current scope
        List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();
        resolvedTypes.addAll(node.getEnclosingScope().resolveSysMLTypeMany(node.getQualifiedName().getReferencedName()));
        node.setResolvedTypes(resolvedTypes);
      }
      else {
        List<SysMLTypeSymbol> resolvedTypes = node.getQualifiedName().resolveSymbols();
        node.setResolvedTypes(resolvedTypes);
      }
    }
    else if (phase == 2) {
      Optional<ASTSysMLName> importAs = Optional.empty();
      Optional<SysMLTypeSymbol> currentType = Optional.empty();
      if (node.isPresentSysMLName()) {
        importAs = Optional.of(node.getSysMLName());
        currentType = Optional.of(node.getSymbol());
      }
      List<CoCoStatus> warnings = this.addToScope(node.getResolvedTypes(), node.getEnclosingScope(), false,
          importAs, currentType, node.getQualifiedName(), node.isAlias());
      node.setWarnings(warnings);
    }
    else {
      Log.error("Internal error in " + this.getClass().getName() + " Do not call this Visitor with ast.accept. There are explicit methods.");
    }
  }

  @Override
  public void visit(ASTImportUnitStd node) {
    if (phase == 1) {
      List<SysMLTypeSymbol> resolvedTypes = node.getQualifiedName().resolveSymbols();
      node.setResolvedTypes(resolvedTypes);
    }
    else if (phase == 2) {
      Optional<ASTSysMLName> importAs = Optional.empty();
      Optional<SysMLTypeSymbol> currentType = Optional.empty();
      if (node.isPresentSysMLName()) {
        importAs = Optional.of(node.getSysMLName());
        currentType = Optional.of(node.getSymbol());
      }
      List<CoCoStatus> warnings = this.addToScope(node.getResolvedTypes(),
          node.getEnclosingScope(), node.isStar(), importAs, currentType, node.getQualifiedName(), false);
      node.setWarnings(warnings);
    }
    else {
      Log.error("Internal error in " + this.getClass().getName() + " Do not call this Visitor with ast.accept. There are explicit methods.");
    }
  }

  public List<CoCoStatus> addToScope(List<SysMLTypeSymbol> resolvedTypes, ISysMLNamesBasisScope scopeToAddTo,
      boolean starImport, Optional<ASTSysMLName> importAs, Optional<SysMLTypeSymbol> importAsCorrespondingSymbol,
      ASTQualifiedName importName, boolean onlyAlias) {
    List<CoCoStatus> warnings = new ArrayList<>();
    if(importIsOfTypeKerML(resolvedTypes, importName)){

    }
    else if (resolvedTypes.size() == 0) {
      warnings.add(new CoCoStatus(SysMLCoCoName.ImportIsDefined, "Could not resolve import \"" + importName.getFullQualifiedName() + "\"."));
    }
    else if (resolvedTypes.size() == 1 && resolvedTypes.get(0).getAstNode() instanceof ASTPackage) {
      //Importing a package.
      ASTPackage astPackage = (ASTPackage) resolvedTypes.get(0).getAstNode();
      ISysMLNamesBasisScope importThis = astPackage.getPackageBody().getSpannedScope();
      if (starImport && importAs.isPresent()) {
        warnings.add(new CoCoStatus(SysMLCoCoName.ImportWithStarAndWithAs, "Cannot star import package with an alias \"as " + importAs.get().getName() + "\"."));
      }
      else if (starImport) {
        LinkedListMultimap<String, SysMLTypeSymbol> imports = importThis.getSysMLTypeSymbols();
        for (SysMLTypeSymbol importSymbol : imports.values()) {
          if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, importSymbol.getName(), warnings, false)) {
            scopeToAddTo.add(importSymbol);
          }
        }
      }
      else {
        warnings.add(new CoCoStatus(SysMLCoCoName.PackageImportWithoutStar, "Importing a package without a star (e.g.\"::*\") will have no effect. " + "If this Statement imports something else then a scope, this has no effect."));
      }
    }
    else if (resolvedTypes.size() > 1) {
      warnings.add(new CoCoStatus(SysMLCoCoName.AmbiguousImport, "The import statement was ambiguous, nothing will be imported. "));
    }
    else if (resolvedTypes.size() == 1) {
      if (importAs.isPresent()) {
        if (!importAsCorrespondingSymbol.isPresent()) {
          Log.error("Internal error \"Import as, but no given TypeSymbol\" in " + this.getClass().getName());
        }
        else {
          if(onlyAlias){
            importAsCorrespondingSymbol.get().setAstNode(resolvedTypes.get(0).getAstNode());
          }else if(!isAlreadyInScopeAndAddWarning(scopeToAddTo, importAs.get().getName(), warnings ,true)){
            importAsCorrespondingSymbol.get().setAstNode(resolvedTypes.get(0).getAstNode());
          }

        }
      }
      else {
        if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, resolvedTypes.get(0).getName(), warnings, false)) {
          scopeToAddTo.add(resolvedTypes.get(0));
        }
      }

    }
    else {
      Log.error("Internal Error in " + AddImportToScopeVisitor.class.getName() + ". Unexpected case.");
    }
    return warnings;
  }

  private boolean isAlreadyInScopeAndAddWarning(ISysMLNamesBasisScope scopeToAddTo, String name,
      List<CoCoStatus> warnings, boolean importAs) {
    if (name.equals("")) {
      return true;
    }
    if (scopeToAddTo.resolveSysMLTypeMany(name).size() != 0 && !importAs) {
      String scopeName = "";
      if (scopeToAddTo.isPresentName()) {
        scopeName = scopeToAddTo.getName();
      }
      warnings.add(new CoCoStatus(SysMLCoCoName.ImportedElementNameAlreadyExists,
          "The element \"" + name + "\" could not be imported, because it already exists in the scope " + scopeName + "."));
      return true;
    }else if(scopeToAddTo.resolveSysMLTypeMany(name).size() != 1 && importAs){
      //We should have exactly on type,
      // because of import AS
      String scopeName = "";
      if (scopeToAddTo.isPresentName()) {
        scopeName = scopeToAddTo.getName();
      }
      warnings.add(new CoCoStatus(SysMLCoCoName.ImportedElementNameAlreadyExists,
          "The element \"" + name + "\" could not be imported, because it already exists in the scope " + scopeName + "."));
      return true;
    }
    return false;
  }


  private boolean importIsOfTypeKerML(List<SysMLTypeSymbol> resolvedTypes, ASTQualifiedName importName) {
    // This is of course not complete, but supports most common KerML-imports of SysML.
    if(importName.getReferencedName().equals("ScalarValues")){
      return true;
    }else if(importName.getReferencedName().equals("ScalarFunctions")){
      return true;
    }else {
      return false;
    }
  }
}
