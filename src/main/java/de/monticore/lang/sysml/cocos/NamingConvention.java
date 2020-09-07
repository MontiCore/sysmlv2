package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.basics.classifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.basics.classifiers._cocos.ClassifiersASTClassifierDeclarationCompletionStdCoCo;
import de.monticore.lang.sysml.basics.interfaces.namesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTClassifierDeclarationCompletion;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._cocos.SysMLCommonBasisASTClassifierDeclarationCompletionCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.names._ast.ASTSysMLNameDefault;
import de.monticore.lang.sysml.sysml._ast.ASTSysMLNode;
import de.monticore.lang.sysml.sysml._cocos.SysMLASTSysMLNodeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NamingConvention implements ClassifiersASTClassifierDeclarationCompletionStdCoCo {

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
