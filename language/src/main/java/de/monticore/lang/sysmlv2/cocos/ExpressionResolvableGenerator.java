/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.se_rwth.commons.logging.Log;

public class ExpressionResolvableGenerator implements ExpressionsBasisASTNameExpressionCoCo {
  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();


  @Override
  public void check(ASTNameExpression node) {
    var scope = node.getEnclosingScope();
    var parent = scope.getAstNode();

    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      if(!resolveInParts(node))
        Log.error("Could not resolve attribute usage with the name \"" + node.getName() + "\" in a expression.");
    }
    if(parent instanceof ASTStateUsage || parent instanceof ASTStateDef) {
      if(!resolveInBehaviour(node, (SysMLv2Scope) node.getEnclosingScope()))
        Log.error("Could not resolve attribute usage with the name \"" + node.getName() + "\" in a expression.");
    }
    if(parent instanceof ASTActionUsage || parent instanceof ASTActionDef) {
      if(!resolveInBehaviour(node, (SysMLv2Scope) node.getEnclosingScope()))
        Log.error("Could not resolve attribute usage with the name \"" + node.getName() + "\" in a expression.");
    }

  }

  boolean resolveInParts(ASTNameExpression node) {
    var scope = (SysMLv2Scope) node.getEnclosingScope();
    if(scope.resolveAttributeUsageDown(node.getName()).isPresent())
      return true;
    var attributesListPart = attributeResolveUtils.getAttributesOfElement((ASTSysMLElement) scope.getAstNode());
    return attributesListPart.stream().anyMatch(t -> t.getName().equals(node.getName()));
  }

  boolean resolveInBehaviour(ASTNameExpression node, SysMLv2Scope scope) {
    var parent = scope.getAstNode();
    if(parent instanceof ASTSysMLModel || parent instanceof ASTSysMLPackage)
      return false;

    if(scope.resolveAttributeUsageDown(node.getName()).isPresent())
      return true;
    var attributesListPart = attributeResolveUtils.getAttributesOfElement((ASTSysMLElement) parent);
    if(attributesListPart.stream().anyMatch(t -> t.getName().equals(node.getName())))
      return true;
    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      return resolveInParts(node);
    }
    return resolveInBehaviour(node, (SysMLv2Scope) parent.getEnclosingScope());
  }
}
