package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLQualifiedName;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLQualifiedNameSeparator;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLImportStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class ImportSeparatorCoCo
    implements SysMLImportsAndPackagesASTSysMLImportStatementCoCo {

  @Override
  public void check(ASTSysMLImportStatement node) {
    ASTSysMLQualifiedName qualifiedName = node.getSysMLQualifiedName();

    for (ASTSysMLQualifiedNameSeparator separator
        : qualifiedName.getSeparatorList()) {

      if (separator.isPresentDot()) {
        Log.error(
            "0xSYSML119C Imports must use '::' as separator. "
                + "The '.' separator is not allowed.",
            node.get_SourcePositionStart()
        );
      }
    }
  }
}
