/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTAssignmentActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTDecideAction;
import de.monticore.lang.sysmlactions._ast.ASTForkAction;
import de.monticore.lang.sysmlactions._ast.ASTJoinAction;
import de.monticore.lang.sysmlactions._ast.ASTLoopAction;
import de.monticore.lang.sysmlactions._ast.ASTMergeAction;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirst;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ActionGeneratorCoCos implements SysMLActionsASTActionDefCoCo, SysMLActionsASTActionUsageCoCo {

  /**
   * Check that all super types (specializations) exist. They need to be Action definitions.
   */
  @Override
  public void check(ASTActionDef node) {

    //
  }

  /**
   * Check that all super types (specializations) exist. They might be Action definitions or usages.
   */
  @Override
  public void check(ASTActionUsage node) {
    int firstCount = 0;
    if(!(node instanceof ASTJoinAction || node instanceof ASTDecideAction || node instanceof ASTForkAction
        || node instanceof ASTMergeAction || node instanceof ASTLoopAction || node instanceof ASTSendActionUsage || node instanceof ASTAssignmentActionUsage)) {
      for (ASTSysMLElement x : node.getSysMLElementList()) {
        if(x instanceof ASTSysMLFirst) {
          firstCount++;
          if(!((ASTSysMLFirst) x).getName().equals("start")) {
            Log.error("Action first usage has to use the name \" start\".");
          }
        }
      }
      if(firstCount != 1) {
        Log.error("ActionUsage " + node.getName() + " has " + firstCount + " \"first\" usage, but needs exactly 1.");
      }
    }
    else {
      checkControlNodes(node);
    }

    if(node.streamSysMLElements().anyMatch(
        t -> t instanceof ASTPortUsage | t instanceof ASTPartUsage | t instanceof ASTPortDef
            | t instanceof ASTPartDef))
      Log.error("ActionUsage " + node.getName() + " has port/part usages/defs as sub elements, this is not allowed.");
    if(node.streamSysMLElements().filter(t -> t instanceof ASTAttributeUsage).filter(
        t -> ((ASTAttributeUsage) t).isPresentSysMLFeatureDirection()).filter(
        t -> !((ASTAttributeUsage) t).getSysMLFeatureDirection().equals(ASTSysMLFeatureDirection.OUT)).anyMatch(
        t -> !((ASTAttributeUsage) t).isPresentExpression())) {
      Log.error("Directed Attributes of " + node.getName() + " have no expression, but need one.");

    }
  }

  void checkControlNodes(ASTActionUsage node) {
    boolean presentName = false;
    List<ASTSpecialization> specialicationList = new ArrayList<>();
    if(node instanceof ASTJoinAction || node instanceof ASTDecideAction || node instanceof ASTForkAction
        || node instanceof ASTMergeAction || node instanceof ASTSendActionUsage || node instanceof ASTAssignmentActionUsage) {
      presentName = node.isPresentName();
      specialicationList = node.getSpecializationList();
    }
    if(!presentName) {
      Log.error("ActionUsage  has no name, but needs a name.");
    }
    if(!specialicationList.isEmpty()) {
      Log.error("ActionUsage  has specialications, this is not allowed.");
    }
  }
}
