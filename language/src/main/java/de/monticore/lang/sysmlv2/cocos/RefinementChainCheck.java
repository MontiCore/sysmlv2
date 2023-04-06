package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.SourcePositionBuilder;
import de.se_rwth.commons.logging.Log;

public class RefinementChainCheck implements SysMLPartsASTPartDefCoCo {

  @Override
  public void check(ASTPartDef node) {
    SourcePosition partDefNameEnd = new SourcePositionBuilder().setFileName(node.get_SourcePositionStart().getFileName().get())
        .setLine(node.get_SourcePositionStart().getLine())
        .setColumn(node.get_SourcePositionStart().getColumn() + node.getName().length() + "part def".length() + 2)
        .build();

    if (node.getRefinements().isEmpty() && node.getSymbol().getRequirementType() == ASTSysMLReqType.LLR){
      Log.warn("0x9001 Low level requirement without refinement.", node.get_SourcePositionStart(), partDefNameEnd);
    }

    if (node.getSymbol().getRequirementType() == ASTSysMLReqType.HLR){
      var refiners = node.getSymbol().getRefiners();
      if (refiners.findAny().isEmpty()){
        Log.warn("0x9002 High level requirements should be refined.", node.get_SourcePositionStart(), partDefNameEnd);
      }
    }
  }
}
