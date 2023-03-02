/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.generator.helper.ActionsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActionsUtils {
  ActionsHelper actionsHelper = new ActionsHelper();

  /**
   * Code template reference
   */
  protected final CD4C cd4C;

  public ActionsUtils() {
    this.cd4C = CD4C.getInstance();
  }

  public void createActionsForPart(ASTPartUsage partUsage, ASTCDClass astcdClass) {
    partUsage.streamSysMLElements().filter(t -> t instanceof ASTActionUsage).map(t -> (ASTActionUsage) t).forEach(
        t -> createAction(t, astcdClass));
  }
  public void createActionsForPart(ASTPartDef partUsage, ASTCDClass astcdClass) {
    partUsage.streamSysMLElements().filter(t -> t instanceof ASTActionUsage).map(t -> (ASTActionUsage) t).forEach(
        t -> createAction(t, astcdClass));
  }

  public void createActionsForInterface(ASTSysMLElement element, ASTCDInterface anInterface) {
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(element instanceof ASTPartDef) {
      elementList = ((ASTPartDef) element).getSysMLElementList();
    }
    if(element instanceof ASTPartUsage) {
      elementList = ((ASTPartUsage) element).getSysMLElementList();
    }

    elementList.stream().filter(t -> t instanceof ASTActionUsage).map(t -> (ASTActionUsage) t).forEach(
        t -> createAction(t, anInterface));
  }

  public void createAction(ASTActionUsage actionUsage, ASTCDClass astcdClass) {
    actionUsage.streamSysMLElements().filter(t -> t instanceof ASTActionUsage).map(t -> (ASTActionUsage) t).forEach(
        t -> createAction(t, astcdClass));
    cd4C.addMethod(astcdClass, "sysml2cd.actions.ActionMethod", actionUsage, getParameterList(actionUsage),
        getAttributeList(actionUsage), false);
    if(actionsHelper.hasActionDecideMerge(actionUsage) &&
        actionsHelper.isMergeNode(actionsHelper.getFirstControlNode(actionUsage))) {
      cd4C.addMethod(astcdClass, "sysml2cd.actions.DecideMethod", actionUsage, getParameterList(actionUsage),
          getAttributeList(actionUsage), false);
    }
  }

  public void createAction(ASTActionUsage actionUsage, ASTCDInterface anInterface) {
    actionUsage.streamSysMLElements().filter(t -> t instanceof ASTActionUsage).map(t -> (ASTActionUsage) t).forEach(
        t -> createAction(t, anInterface));
    cd4C.addMethod(anInterface, "sysml2cd.actions.ActionMethod", actionUsage, getParameterList(actionUsage),
        getAttributeList(actionUsage), true);
    if(actionsHelper.hasActionDecideMerge(actionUsage) &&
        actionsHelper.isMergeNode(actionsHelper.getFirstControlNode(actionUsage))) {
      cd4C.addMethod(anInterface, "sysml2cd.actions.DecideMethod", actionUsage, getParameterList(actionUsage),
          getAttributeList(actionUsage), true);
    }
  }

  List<ASTSysMLElement> getParameterList(ASTActionUsage actionUsage) {
    return actionUsage.streamSysMLElements().filter(t -> t instanceof ASTAttributeUsage).filter(
        t -> ((ASTAttributeUsage) t).isPresentSysMLFeatureDirection()).collect(Collectors.toList());
  }

  List<ASTSysMLElement> getAttributeList(ASTActionUsage actionUsage) {
    return actionUsage.streamSysMLElements().filter(t -> t instanceof ASTAttributeUsage).filter(
        t -> !((ASTAttributeUsage) t).isPresentSysMLFeatureDirection()).collect(Collectors.toList());
  }

}
