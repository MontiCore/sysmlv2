package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlconnections._ast.ASTBind;
import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTBindCoCo;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTFlowCoCo;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PortResolveUtils;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConnectionGeneratorCoCos implements SysMLConnectionsASTFlowCoCo, SysMLConnectionsASTBindCoCo {
  /**
   * Check that a flow type exists, that the directions fit and src/tgt exist
   */

  private static final int in = 2;

  private static final int out = 4;

  private static final int inout = 3;

  @Override public void check(ASTFlow node) {

    var astNode = node.getEnclosingScope().getAstNode();
    List<ASTPortUsage> usageList = new ArrayList<>();
    if(astNode instanceof ASTPartDef) {
      usageList = checkResolvable(((ASTPartDef) astNode).getSpannedScope(), node.getSource(),
          node.getTarget());
    }
    if(astNode instanceof ASTPartUsage) {

      usageList = checkResolvable(((ASTPartUsage) astNode).getSpannedScope(), node.getSource(),
          node.getTarget());
    }

    if(astNode instanceof ASTSysMLPackage) {
      usageList = checkResolvable((ISysMLPartsScope) ((ASTSysMLPackage) astNode).getSpannedScope(),
          node.getSource(),
          node.getTarget());
    }
    checkPortDirections(usageList.get(0), usageList.get(1));

  }

  @Override
  public void check(ASTBind node) {
    var source = node.getSource();
    var target = node.getTarget();
    Optional<ASTAttributeUsage> sourceResolve = Optional.empty();
    Optional<ASTAttributeUsage> targetResolve = Optional.empty();
    var parent = node.getEnclosingScope().getAstNode();
    if(parent instanceof ASTActionUsage || parent instanceof ASTActionDef || parent instanceof ASTStateDef
        || parent instanceof ASTStateUsage) {
      sourceResolve = AttributeResolveUtils.resolveInBehaviour(source, (SysMLv2Scope) node.getEnclosingScope());
      targetResolve = AttributeResolveUtils.resolveInBehaviour(target, (SysMLv2Scope) node.getEnclosingScope());
    }
    if(parent instanceof ASTPartDef || parent instanceof ASTPartUsage) {
      sourceResolve = AttributeResolveUtils.resolveInParts(source);
      targetResolve = AttributeResolveUtils.resolveInParts(target);
    }
    if(sourceResolve.equals(Optional.empty()))
      Log.error("Source:\"" + source + "\" of Bind can not be resolved.");
    if(targetResolve.equals(Optional.empty()))
      Log.error("Target:\"" + target + "\" of Bind can not be resolved.");

    if(!source.getQName().equals(source.getBaseName()) && !target.getQName().equals(target.getBaseName())){
      Log.error("A bind uses two full qualified names, only can be full qualified.");
    }

  }

  List<ASTPortUsage> checkResolvable(ISysMLPartsScope scope, ASTMCQualifiedName source, ASTMCQualifiedName target) {
    List<ASTPortUsage> usageList = new ArrayList<>();
    var sourceResolvable = PortResolveUtils.resolvePort(source.getQName(), source.getBaseName(), scope);

    var targetResolvable = PortResolveUtils.resolvePort(target.getQName(), target.getBaseName(), scope);
    if(sourceResolvable.isEmpty()) {
      Log.error("Flow from port " + source.getQName() + " is not resolvable.");

    }
    else {
      usageList.add(sourceResolvable.get().getAstNode());
    }
    if(targetResolvable.isEmpty()) {
      Log.error("Flow from port " + target.getQName() + " is not resolvable.");

    }
    else {
      usageList.add(targetResolvable.get().getAstNode());
    }
    return usageList;
  }

  void checkPortDirections(ASTPortUsage source, ASTPortUsage target) {
    ASTAttributeUsage sourceValue = source.getValueAttribute();
    ASTAttributeUsage targetValue = target.getValueAttribute();

    if(sourceValue.getSysMLFeatureDirection().getIntValue() == in && (
        targetValue.getSysMLFeatureDirection().getIntValue() == in
            || targetValue.getSysMLFeatureDirection().getIntValue() == inout)) {
      //target parent needs to be a sub element of source parent
      if(!areParentsSubParts(source, target))
        Log.error("Flow from port " + source.getName() + " to " + target.getName()
            + " is from \"in\" to \"in\"/\"inout\", but they are not sub-elements.");
    }
    if(sourceValue.getSysMLFeatureDirection().getIntValue() == out
        && targetValue.getSysMLFeatureDirection().getIntValue() == in) {
      //target kein sub element of source bzw source kein sub element von target
      if(areParentsSubParts(source, target) || areParentsSubParts(target, source))
        Log.error("Flow from port " + source.getName() + " to " + target.getName()
            + " is from \"out\" to \"in\", but cannot be sub-elements.");
    }
    if(sourceValue.getSysMLFeatureDirection().getIntValue() == out
        && targetValue.getSysMLFeatureDirection().getIntValue() == out) {
      //target sub element of source
      if(!areParentsSubParts(target, source))
        Log.error("Flow from port " + source.getName() + " to " + target.getName()
            + " is from \"out\" to \"out\", but they are not sub-elements.");
    }
    if(sourceValue.getSysMLFeatureDirection().getIntValue() == inout
        && targetValue.getSysMLFeatureDirection().getIntValue() == out) {
      //target sub element of source
      if(!areParentsSubParts(source, target))
        Log.error("Flow from port " + source.getName() + " to " + target.getName()
            + " is from \"inout\" to \"out\", but they are not sub-elements.");
    }

    if(sourceValue.getSysMLFeatureDirection().getIntValue() == in
        && targetValue.getSysMLFeatureDirection().getIntValue() == out) {
      Log.error("Flow from port " + source.getName() + " to " + target.getName()
          + " is from \"in\" to \"out\" this is not allowed.");

    }

  }

  boolean areParentsSubParts(ASTPortUsage parentPort, ASTPortUsage childPort) {
    var parentPortPart = parentPort.getEnclosingScope().getAstNode();
    var childPortPart = childPort.getEnclosingScope().getAstNode();
    List<ASTSysMLElement> sysMLElementList = new ArrayList<>();
    if(parentPortPart instanceof ASTPartDef)
      sysMLElementList = ((ASTPartDef) parentPortPart).getSysMLElementList();
    if(parentPortPart instanceof ASTPartUsage)
      sysMLElementList = ((ASTPartUsage) parentPortPart).getSysMLElementList();

    return sysMLElementList.contains((ASTSysMLElement) childPortPart);

  }

}
