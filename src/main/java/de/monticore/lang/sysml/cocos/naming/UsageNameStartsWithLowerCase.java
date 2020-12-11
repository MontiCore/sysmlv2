package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.common.sysmlclassifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.common.sysmlclassifiers._cocos.SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTUsageDeclaration;
import de.monticore.lang.sysml.common.sysmlusages._cocos.SysMLUsagesASTUsageDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class UsageNameStartsWithLowerCase implements SysMLUsagesASTUsageDeclarationCoCo {

  @Override
  public void check(ASTUsageDeclaration node) {
    if(node.isPresentSysMLNameAndTypePart()) {
      String name = node.getName();
      boolean startsWithLowerCase = Character.isLowerCase(name.charAt(0));
      if (!startsWithLowerCase) {
        Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.UsageNameStartsWithLowerCase) +
            " Name \"" + name + "\" should start with a lower case letter.", node.get_SourcePositionStart());
      }
    }
  }
}