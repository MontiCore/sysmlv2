package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTEntryAction;
import de.monticore.lang.sysmlstates._ast.ASTExitAction;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.lang.sysmlv2.generator.utils.resolve.ActionResolveUtils;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateGeneratorCoCo implements SysMLStatesASTStateUsageCoCo, SysMLStatesASTStateDefCoCo {
  ActionResolveUtils actionResolveUtils = new ActionResolveUtils();

  /**
   * Check that a usage only uses type, specialization or sub eleements
   */
  @Override
  public void check(ASTStateUsage node) {

    if(node.isParalled()) {
      Log.error("Parallel States are currently not supported, but " + node.getName() + " uses it.");
      int parallelStates = 0;
      for (ASTSysMLElement x : node.getSysMLElementList()) {
        if(x instanceof ASTStateUsage) {
          parallelStates++;
        }
      }
      if(parallelStates < 2) {
        Log.error(
            "StateUsage " + node.getName() + " has " + parallelStates + " \"StateUsages\" , but needs at least 2.");
      }
    }
    var specTypes = node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
        ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
    var typeTypes = node.streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
        ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
    if(node.streamSpecializations().anyMatch(t -> t instanceof ASTSysMLRedefinition))
      Log.error("State usage " + node.getName() + " uses redefinition, this is not allowed.");
    if(specTypes.size() > 1)
      Log.error("State usage " + node.getName() + " has more than one specialization, this is not allowed.");
    if(typeTypes.size() > 1)
      Log.error("State usage " + node.getName() + " has more than one type, this is not allowed.");
    if(specTypes.size() > 0 && typeTypes.size() > 0)
      Log.error("State usage " + node.getName() + " has a type and a specialization. It's not allowed to have both.");
    if(node.getSysMLElementList().size() > 0 && (typeTypes.size() > 0 || specTypes.size() > 0))
      Log.error("State usage " + node.getName()
          + " has sub-elements, but uses a type or a specialization. This is not allowed.");
    checkActions(node.getEntryActionList(), node.getExitActionList(), node.getDoActionList());
  }

  /**
   * Check that a def does not use type, specialization or redefinition
   */
  @Override
  public void check(ASTStateDef node) {

    if(node.getSpecializationList().size() > 0)
      Log.error(
          "State def " + node.getName() + " has a specialization, redefinition or type none of these are allowed.");

    checkActions(node.getEntryActionList(), node.getExitActionList(), node.getDoActionList());

  }

  private void checkActions(List<ASTEntryAction> entryActionList,
                            List<ASTExitAction> exitActionList, List<ASTDoAction> doActionList) {
    if(!entryActionList.isEmpty()) {
      if(entryActionList.stream().filter(ASTEntryAction::isPresentAction).anyMatch(
          t -> !resolveAction(t.getAction(), (SysMLv2Scope) t.getEnclosingScope()))) {
        Log.error("State usage has an entry action that is not resolvable.");
      }
    }
    if(!exitActionList.isEmpty()) {
      if(exitActionList.stream().filter(ASTExitAction::isPresentAction).anyMatch(
          t -> !resolveAction(t.getAction(), (SysMLv2Scope) t.getEnclosingScope()))) {
        Log.error("State usage has an entry action that is not resolvable.");
      }
    }
    if(!doActionList.isEmpty()) {
      if(doActionList.stream().filter(ASTDoAction::isPresentAction).anyMatch(
          t -> !resolveAction(t.getAction(), (SysMLv2Scope) t.getEnclosingScope()))){
        Log.error("State usage has an entry action that is not resolvable.");
      }
    }
  }

  boolean resolveAction(String actionName, SysMLv2Scope scope) {
    var parent = scope.getAstNode();
    if(parent instanceof ASTSysMLModel || parent instanceof ASTSysMLPackage)
      return false;

    if(scope.resolveAttributeUsageDown(actionName).isPresent())
      return true;
    var actionsListPart = actionResolveUtils.getActionsOfElement((ASTSysMLElement) parent);
    if(actionsListPart.stream().anyMatch(t -> t.getName().equals(actionName)))
      return true;
    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      return resolveInParts(actionName, scope);
    }
    return resolveAction(actionName, (SysMLv2Scope) parent.getEnclosingScope());
  }

  boolean resolveInParts(String actionName, SysMLv2Scope scope) {
    if(scope.resolveAttributeUsageDown(actionName).isPresent())
      return true;
    var actionsList = actionResolveUtils.getActionsOfElement((ASTSysMLElement) scope.getAstNode());
    return actionsList.stream().anyMatch(t -> t.getName().equals(actionName));
  }
}

