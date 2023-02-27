/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.visitor;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.*;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.generator.helper.ActionsHelper;

import java.util.List;
import java.util.stream.Collectors;

public class Actions2CDVisitor implements SysMLActionsVisitor2 {
ActionsHelper actionsHelper = new ActionsHelper();
  protected ASTCDClass astcdClass;

  /**
   * Code template reference
   */
  protected final CD4C cd4C;

  public Actions2CDVisitor(ASTCDClass astcdClass) {
    this.cd4C = CD4C.getInstance();
    this.astcdClass = astcdClass;
  }

  @Override
  public void visit(ASTActionUsage actionUsage) {
    cd4C.addMethod(astcdClass, "sysml2cd.actions.ActionMethod", actionUsage, getParameterList(actionUsage),
        getAttributeList(actionUsage));
    if(actionsHelper.hasActionDecideMerge(actionUsage) &&
        actionsHelper.isMergeNode(actionsHelper.getFirstControlNode(actionUsage))){
      cd4C.addMethod(astcdClass, "sysml2cd.actions.DecideMethod", actionUsage, getParameterList(actionUsage),
          getAttributeList(actionUsage));
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
