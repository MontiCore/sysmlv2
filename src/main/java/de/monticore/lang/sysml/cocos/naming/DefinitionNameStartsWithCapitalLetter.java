/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.common.sysmlclassifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.common.sysmlclassifiers._cocos.SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class DefinitionNameStartsWithCapitalLetter implements SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo {

  @Override
  public void check(ASTClassifierDeclarationCompletionStd node) {
    String name =  node.getName();
    boolean startsWithUpperCase = Character.isUpperCase(name.charAt(0));
    if(!startsWithUpperCase){
      Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.DefinitionNameStartsWithCapitalLetter)+ " Name \""+name+
          "\" should start with a capital letter.", node.get_SourcePositionStart());
    }
  }
}
