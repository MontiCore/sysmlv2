/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._cocos.SysMLNamesBasisASTQualifiedNameCoCo;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NameReference implements SysMLNamesBasisASTQualifiedNameCoCo {

  @Override
  public void check(ASTQualifiedName qualifiedName) {
    if(qualifiedName.resolveSymbols().size()>0){
      //Log.info("Block could be resolved. " + reference, this.getClass().getName());
    }else {
      Log.error(SysMLCoCos.getErrorCode(SysMLCoCoName.NameReference) + " "+
          "Reference " + qualifiedName.getFullQualifiedName() + " could not be resolved.",  qualifiedName.get_SourcePositionStart());
    }
  }
}
