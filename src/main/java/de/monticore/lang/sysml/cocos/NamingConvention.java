package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.basics.sysmlclassifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.basics.sysmlclassifiers._cocos.SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NamingConvention implements SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo {

  @Override
  public void check(ASTClassifierDeclarationCompletionStd node) {
    /* ASTSysMLName astSysMLName =  node.getName(); //TODO old remove
    if(astSysMLName instanceof ASTSysMLNameDefault){
      ASTSysMLNameDefault nameDefault = (ASTSysMLNameDefault) astSysMLName;
      String name = nameDefault.getName();
      boolean startsWithUpperCase = Character.isUpperCase(name.charAt(0));
      if(!startsWithUpperCase){
        //TODO use String format?
        Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.NamingConvention)+ " Name "+name+
                " should start with a capital letter.",astSysMLName.get_SourcePositionStart());
      }
    }*/
    String name =  node.getName();
    boolean startsWithUpperCase = Character.isUpperCase(name.charAt(0));
    if(!startsWithUpperCase){
      //TODO use String format?
      Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.NamingConvention)+ " Name "+name+
          " should start with a capital letter.", node.get_SourcePositionStart());
    }
  }
}
