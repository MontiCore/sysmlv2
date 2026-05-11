package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.se_rwth.commons.logging.Log;

public class ConnectedVariableExistsCoCo implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node){

    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }
    ISysMLPartsScope scope = node.getEnclosingScope();
    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();

    String srcQName = src.getMCQualifiedName().toString();
    String tgtQName = tgt.getMCQualifiedName().toString();
    if(!srcQName.contains(".")) {
      if(scope.resolveVariable(srcQName).isEmpty()){
        Log.error("0x10AD0 Illegal connection: The source Variable with " +
          "the name " + srcQName + " can not be resolved from the scope " + scope.getName(),
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
        );return;
      }
    }
    if(!tgtQName.contains(".")) {
      if(scope.resolveVariable(tgtQName).isEmpty()){
        Log.error("0x10AD0 Illegal connection: The target Variable with " +
          "the name " + tgtQName + " can not be resolved from the scope " + scope.getName(),
            node.get_SourcePositionStart(),node.get_SourcePositionEnd()
        );return;
      }
    }
  }
}
