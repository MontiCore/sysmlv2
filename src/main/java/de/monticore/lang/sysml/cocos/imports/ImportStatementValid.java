package de.monticore.lang.sysml.cocos.imports;

import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTImportUnitStdCoCo;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ImportStatementValid implements SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo,
    SysMLImportsAndPackagesASTImportUnitStdCoCo {
  @Override
  public void check(ASTAliasPackagedDefinitionMember node) {
    for (CoCoStatus cocoStatus : node.getWarnings()) {
      if(!cocoStatus.throwError()){
        Log.warn(SysMLCoCos.getErrorCode(cocoStatus.getCoCoName()) + " " +
            cocoStatus.getMessage(), node.get_SourcePositionStart());
      }else {
        Log.error(SysMLCoCos.getErrorCode(cocoStatus.getCoCoName()) + " " +
            cocoStatus.getMessage(), node.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTImportUnitStd node) {
    for (CoCoStatus cocoStatus : node.getWarnings()) {
      if(!cocoStatus.throwError()){
        Log.warn(SysMLCoCos.getErrorCode(cocoStatus.getCoCoName()) + " " +
            cocoStatus.getMessage(), node.get_SourcePositionStart());
      }else {
        Log.error(SysMLCoCos.getErrorCode(cocoStatus.getCoCoName()) + " " +
            cocoStatus.getMessage(), node.get_SourcePositionStart());
      }
    }
  }
}
