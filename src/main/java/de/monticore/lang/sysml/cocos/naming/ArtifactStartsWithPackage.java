/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._cocos.SysMLSharedASTUnitCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTRootNamespace;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ArtifactStartsWithPackage implements SysMLSharedASTUnitCoCo {
  @Override
  public void check(ASTUnit node) {
    if(!(node instanceof ASTRootNamespace)){
      Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.ArtifactStartsWithPackage) +
              " Each artifact should start with its a package to organize the model elements." ,
          node.get_SourcePositionStart());
    }
  }
}
