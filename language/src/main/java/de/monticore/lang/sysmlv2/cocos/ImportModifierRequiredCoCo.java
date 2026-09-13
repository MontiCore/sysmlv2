package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLImportStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class ImportModifierRequiredCoCo implements SysMLImportsAndPackagesASTSysMLImportStatementCoCo {

  @Override
  public void check(ASTSysMLImportStatement node) {
    if (node.getModifier() == null) {
      Log.error("0xSYSML119A Import must declare a modifier (public/private/protected).",
          node.get_SourcePositionStart());
      return;
    }

    var modifier = node.getModifier();
    boolean valid = modifier.isPublic() || modifier.isPrivate() || modifier.isProtected();

    if (!valid) {
      Log.error("0xSYSML119B Import modifier must be one of: public, private, protected.",
          node.get_SourcePositionStart());
    }
  }
}
