package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.se_rwth.commons.logging.Log;

public class ConnectedVariableExistsCoCo implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node){
    ISysMLPartsScope scope = node.getEnclosingScope();
    String srcQName = node.getSrc().getMCQualifiedName().toString();
    String tgtQName = node.getTgt().getMCQualifiedName().toString();

    if(!srcQName.contains(".")) {
      if(scope.resolveVariable(srcQName).isEmpty()){
        Log.error("0x10AD0 Cannot resolve the element named \"" + srcQName
            + "\"", node.get_SourcePositionStart(),node.get_SourcePositionEnd()
        );
        return;
      }
    }
    if(!tgtQName.contains(".")) {
      if(scope.resolveVariable(tgtQName).isEmpty()){
        Log.error("0x10AD1 Cannot resolve the element named \"" + tgtQName
            + "\"", node.get_SourcePositionStart(),node.get_SourcePositionEnd()
        );
        return;
      }
    }
  }
}
