package de.monticore.lang.sysml.sysml._symboltable.doubleimports;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;

import java.util.ArrayList;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AmbigousImportCheck implements SysMLInheritanceVisitor {

  public void addWarningForAmbigousImport(ASTUnit ast) {
    ast.accept(this);
  }

  @Override
  public void visit(ASTAliasPackagedDefinitionMember node) {
    if (node.getResolvedTypes().size() >= 2) {
      node.getWarnings().add(new CoCoStatus(SysMLCoCoName.AmbiguousImport, "The import statement was ambiguous, nothing"
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
        node.getWarnings().add(new CoCoStatus(SysMLCoCoName.AmbiguousImport, "The import statement was ambiguous, "
            + "nothing will " + "be" + " imported. "));
        node.setResolvedTypes(new ArrayList<>());
      }
    }
  }

}
