package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTFlowCoCo;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PortResolveUtils;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConnectionGeneratorCoCos implements SysMLConnectionsASTFlowCoCo {
  /**
   * Check that a flow type exists, that the directions fit and src/tgt exist
   */
  PortResolveUtils portResolveUtils = new PortResolveUtils();

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

  List<ASTPortUsage> checkResolvable(ISysMLPartsScope scope, ASTMCQualifiedName source, ASTMCQualifiedName target) {
    List<ASTPortUsage> usageList = new ArrayList<>();
    var sourceResolvable = resolvePort(source.getQName(), source.getBaseName(), scope);

    var targetResolvable = resolvePort(target.getQName(), target.getBaseName(), scope);
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

  Optional<PortUsageSymbol> resolvePort(String QName, String BaseName, ISysMLPartsScope scope) {
    String parentPart;
    if(QName.equals(BaseName)) {
      parentPart = QName;
    }
    else {
      parentPart = QName.substring(0, QName.length() - BaseName.length() - 1);
    }

    var parentPartDef = scope.resolvePartDefDown(parentPart);
    var parentPartUsage = scope.resolvePartUsageDown(parentPart);
    //check first if its present in transitive supertypes of parts
    if(parentPartUsage.isPresent()) {
      var portUsageList = portResolveUtils.getPortsOfElement(parentPartUsage.get().getAstNode());
      if(portUsageList.stream().anyMatch(t -> t.getName().equals(BaseName)))
        return portUsageList.stream().filter(t -> t.getName().equals(BaseName)).map(t -> t.getSymbol()).findFirst();
    }
    if(parentPartDef.isPresent()) {
      var portUsageList = portResolveUtils.getPortsOfElement(parentPartDef.get().getAstNode());
      if(portUsageList.stream().anyMatch(t -> t.getName().equals(BaseName)))
        return portUsageList.stream().filter(t -> t.getName().equals(BaseName)).map(t -> t.getSymbol()).findFirst();
    }

    return scope.resolvePortUsageDown(QName);

  }

  void checkPortDirections(ASTPortUsage source, ASTPortUsage target) {
    ASTAttributeUsage sourceValue = source.getValueAttribute();
    ASTAttributeUsage targetValue = target.getValueAttribute();

    if(sourceValue.getSysMLFeatureDirection().getIntValue() == in && (targetValue.getSysMLFeatureDirection().getIntValue() == in
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
