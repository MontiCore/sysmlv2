/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.sysml._symboltable.doubleimports;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverserImplementation;

import java.util.ArrayList;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AmbigousImportCheck implements SysMLImportsAndPackagesVisitor2 {

  SysMLTraverser traverser = null;

  public AmbigousImportCheck(){}

  public AmbigousImportCheck(SysMLTraverser traverser) {
    this.traverser=traverser;
    this.traverser.add4SysMLImportsAndPackages(this);
  }

  public void init() {
    if(traverser != null)
      return;
    this.traverser = new SysMLTraverserImplementation();
    traverser.add4SysMLImportsAndPackages(this);
  }

  public void addWarningForAmbigousImport2of5(ASTUnit ast) {
    init();
    ast.accept(traverser);
  }

  @Override
  public void visit(ASTAliasPackagedDefinitionMember node) {
    if (node.getResolvedTypes().size() >= 2) {
      node.getWarnings().add(new CoCoStatus(SysMLCoCoName.NoAmbiguousImport, "The import statement was ambiguous, nothing"
          + " will " + "be" + " imported. "));
      node.setResolvedTypes(new ArrayList<>());
    }
  }

  @Override
  public void visit(ASTImportUnitStd node) {
    if (node.getResolvedTypes().size() >= 2) {
      boolean isOnlyPackage = true;

      for (SysMLTypeSymbol s : node.getResolvedTypes()) {
        if (!(s.getAstNode() instanceof ASTPackage)) {
          isOnlyPackage = false;
        }
      }
      if (!isOnlyPackage) {
        node.getWarnings().add(new CoCoStatus(SysMLCoCoName.NoAmbiguousImport, "The import statement was ambiguous, "
            + "nothing will " + "be" + " imported. "));
        node.setResolvedTypes(new ArrayList<>());
      }
    }
  }

}
