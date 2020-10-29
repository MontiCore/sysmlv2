package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._cocos.SysMLNamesBasisASTQualifiedNameCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NameReference implements SysMLNamesBasisASTQualifiedNameCoCo {

  @Override
  public void check(ASTQualifiedName qualifiedName) {
    String reference = qualifiedName.getReferencedName();

    Log.info("Checking to resolve name " + reference, this.getClass().getName());
    ISysMLNamesBasisScope scope =  qualifiedName.getEnclosingScope();
    Optional<SysMLTypeSymbol> type = scope.resolveSysMLType(reference);
    if(type.isPresent()){
      Log.info("Block could be resolved. " + reference, this.getClass().getName());
    }else {
      Log.error(SysMLCoCos.getErrorCode(SysMLCoCoName.NameReference) + " "+
          "Reference " + reference + " could not be resolved.",  qualifiedName.get_SourcePositionStart());
    }
  }
}
