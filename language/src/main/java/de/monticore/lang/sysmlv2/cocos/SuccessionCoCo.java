package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTSysMLSuccessionCoCo;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
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
    boolean stateSrcPresent = false;
    if(node.isPresentSrc()) {
      srcName = node.getSrc();
      actionSrcPresent = node.getEnclosingScope().resolveActionUsage(node.getSrc()).isPresent();
      if(node.getEnclosingScope() instanceof ISysMLStatesScope) {
        stateSrcPresent = ((ISysMLStatesScope) node.getEnclosingScope()).resolveStateUsage(node.getSrc()).isPresent();
      }
      if(!node.getSrc().equals("start")) {
        if(!actionSrcPresent && !stateSrcPresent) {
          Log.error("Could not find Action or state usage with the name \"" + node.getSrc() + "\" for "
              + srcName + ".");
        }
      }
    }
    else {
      Log.error("There is no src definition for the succession.");
    }
    boolean actionTgtPresent;
    boolean stateTgtPresent = false;
    actionTgtPresent = node.getEnclosingScope().resolveActionUsage(node.getTgt()).isPresent();
    if(node.getEnclosingScope() instanceof ISysMLStatesScope) {
      stateTgtPresent = ((ISysMLStatesScope) node.getEnclosingScope()).resolveStateUsage(node.getTgt()).isPresent();
    }
    if(!actionTgtPresent && !stateTgtPresent) {
      Log.error("Could not find Action or state usage with the name \"" + node.getTgt() + "\" for "
          + srcName + ".");
    }

    if(srcName.equals(node.getTgt())) {
      Log.error("Source and target of succession must be different.");

    }
    //TODO add check that guard is a boolean
  }

}
