package de.monticore.lang.sysml.sysml._symboltable.doubleimports;

import de.monticore.lang.sysml.basics.interfaces.sysmlimportbasis._ast.ASTImportUnit;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._symboltable.ISysMLImportsAndPackagesScope;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLScope;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import de.se_rwth.commons.SourcePosition;

import java.util.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class RemoveDoubleImportsFromScope implements SysMLInheritanceVisitor {
  //Works together with
  // AddImportToScopeVisitor

  public void removeDoubleImportsAndAddWarning3of4(ASTUnit ast) {
    ast.accept(this);
  }

  @Override
  public void visit(ASTPackage astPackage) {
    List<ASTAliasPackagedDefinitionMember> aliasImports = new ArrayList<>();
    List<ASTImportUnitStd> importUnits = new ArrayList<>();

    for (ASTPackageMember member : astPackage.getPackageBody().getPackageMemberList()) {

      //Now if we have a public import add all the symbols also to the AST.
      if (member.isPresentPackagedDefinitionMember()) {
        if (member.getPackagedDefinitionMember() instanceof ASTAliasPackagedDefinitionMember) {
          ASTAliasPackagedDefinitionMember aliasOrImport =
              (ASTAliasPackagedDefinitionMember) member.getPackagedDefinitionMember();

          aliasImports.add(aliasOrImport);
        }
      }
    }
    for (ASTImportUnit importUnit : astPackage.getPackageBody().getImportUnitList()) {
      if (importUnit instanceof ASTImportUnitStd) {
        ASTImportUnitStd importUnitStd = (ASTImportUnitStd) importUnit;
        importUnits.add(importUnitStd);
      }
    }

    //Now collect all imports.
    List<SysMLTypeSymbol> allImports = new ArrayList<>();
    for (ASTAliasPackagedDefinitionMember alias : aliasImports) {
      addSysMLTypeSymbolsToList(allImports, alias.getResolvedTypes());
      addSysMLTypeSymbolsToList(allImports, alias.getTransitiveImports());
    }
    for (ASTImportUnitStd importUnitStd : importUnits) {
      addSysMLTypeSymbolsToList(allImports, importUnitStd.getResolvedTypes());
      addSysMLTypeSymbolsToList(allImports, importUnitStd.getTransitiveImports());
    }

    //Now check for duplicate imports.
    List<SysMLTypeSymbol> removeTheseAndAddWarning = new ArrayList<>();
    for (ASTAliasPackagedDefinitionMember alias : aliasImports) {
      alias.getWarnings().addAll(checkForRemoveTheseAndAddWarning(allImports, alias.getResolvedTypes(),
          alias.get_SourcePositionStart()));
      alias.getWarnings().addAll(checkForRemoveTheseAndAddWarning(allImports, alias.getTransitiveImports(),
          alias.get_SourcePositionStart()));
    }
    for (ASTImportUnitStd importUnit : importUnits) {
      importUnit.getWarnings().addAll(checkForRemoveTheseAndAddWarning(allImports, importUnit.getResolvedTypes(),
          importUnit.get_SourcePositionStart()));
      importUnit.getWarnings().addAll(checkForRemoveTheseAndAddWarning(allImports, importUnit.getTransitiveImports(),
          importUnit.get_SourcePositionStart()));
    }

    //Remove and add warning.
  }

  private List<CoCoStatus> checkForRemoveTheseAndAddWarning(List<SysMLTypeSymbol> checkList, List<SysMLTypeSymbol> checkThese,
      SourcePosition sourcePosition) {
    List<CoCoStatus> warnings = new ArrayList<>();
    Iterator<SysMLTypeSymbol> i = checkThese.iterator();
    while (i.hasNext()){
      SysMLTypeSymbol current = i.next();
      if(checkIfSymbolIsDuplicateAndNotSame(checkList, current)){
        i.remove();
        warnings.add(new CoCoStatus(SysMLCoCoName.DoubleImportOfDifferentSymbolsSameName,
            "Did not import symbol " + current.getName() + "\", "
            + "because a symbol with "
            + "the " + "same "  + "name also gets "
            + "imported into the scope.") );
      }
    }
    return warnings;
  }

  private void addSysMLTypeSymbolsToList(List<SysMLTypeSymbol> addTo, List<SysMLTypeSymbol> addFrom) {
    for (SysMLTypeSymbol s : addFrom) {
      addTo.add(s);
    }
  }
  private boolean checkIfSymbolIsDuplicateAndNotSame(List<SysMLTypeSymbol> checkHere, SysMLTypeSymbol checkForThis){
    for (SysMLTypeSymbol potentialDuplicate : checkHere) {
      //System.out.println("Checking if imported symbol " + checkForThis.getName() +
      //    " has different name or is the same symbol as " + potentialDuplicate.getName());
      if(checkForThis.getName().equals(potentialDuplicate.getName()) && potentialDuplicate != checkForThis){
        //System.out.println("Removing");
        return true;
      }
    }
    return false;
  }

}
