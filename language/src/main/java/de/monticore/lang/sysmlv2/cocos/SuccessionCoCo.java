package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTSysMLSuccessionCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

public class SuccessionCoCo implements SysMLActionsASTSysMLSuccessionCoCo {
  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  @Override
  public void check(ASTSysMLSuccession node) {
    String srcName = "";
    boolean actionSrcPresent;
    if(node.isPresentSrc()) {
      srcName = node.getSrc();
      actionSrcPresent = node.getEnclosingScope().resolveActionUsage(node.getSrc()).isPresent();

      if(!node.getSrc().equals("start")) {
        if(!actionSrcPresent) {
          Log.error("Could not find action usage with the name \"" + node.getSrc() + "\" for "
              + srcName + ".");
        }
      }

    }
    else {
      Log.error("There is no src definition for the succession.");
    }
    boolean actionTgtPresent;
    actionTgtPresent = node.getEnclosingScope().resolveActionUsage(node.getTgt()).isPresent();
    if(!node.getTgt().equals("done")) {
      if(!actionTgtPresent) {
        Log.error("Could not find action usage with the name \"" + node.getTgt() + "\" for "
            + srcName + ".");
      }
    }

    if(srcName.equals(node.getTgt())) {
      Log.error("Source and target of succession must be different.");

    }
    //TODO add check that guard is a boolean
  }

}
