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
    ASTSysMLName astSysMLName =  node.getName();
    if(astSysMLName instanceof ASTSysMLNameDefault){
      ASTSysMLNameDefault nameDefault = (ASTSysMLNameDefault) astSysMLName;
      String name = nameDefault.getName();
      boolean startsWithUpperCase = Character.isUpperCase(name.charAt(0));
      if(!startsWithUpperCase){
       // Log.warn(String.format("'%e' Name '%n' should start with a capital letter.",
         // TODO   SysMLCoCos.getErrorCode(SysMLCoCoName.NamingConvention), name), astSysMLName.get_SourcePositionStart
        // ());
       /* Log.warn( SysMLCoCos.getErrorCode(SysMLCoCoName.NamingConvention)+ " Name "+name+" should start with a "
            + "capital "
            + "letter.", astSysMLName.get_SourcePositionStart()); */
        Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.NamingConvention)+ " Name "+name+
                " should start with a capital letter.",astSysMLName.get_SourcePositionStart());
      }
    }
  }
}
