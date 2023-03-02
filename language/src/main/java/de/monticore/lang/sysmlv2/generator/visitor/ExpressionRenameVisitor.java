package de.monticore.lang.sysmlv2.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRenameVisitor implements ExpressionsBasisVisitor2 {
  public ExpressionRenameVisitor(List<ASTAttributeUsage> parentAttributeList) {
    this.parentAttributeList = parentAttributeList;
  }
  List<ASTAttributeUsage> parentAttributeList = new ArrayList<>();

  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();

  @Override
  public void visit(ASTNameExpression nameExpression) {
   var attributeUsage = resolveInBehaviour(nameExpression, (SysMLv2Scope) nameExpression.getEnclosingScope());
    if(parentAttributeList.contains(attributeUsage)){
      nameExpression.setName("this.getParentPart()."+nameExpression.getName());
    }
  }

  ASTAttributeUsage resolveInParts(ASTNameExpression node) {
    var scope = (SysMLv2Scope) node.getEnclosingScope();
    if(scope.resolveAttributeUsageDown(node.getName()).isPresent())
      return scope.resolveAttributeUsageDown(node.getName()).get().getAstNode();
    var attributesListPart = attributeResolveUtils.getAttributesOfElement((ASTSysMLElement) scope.getAstNode());
    var attribute = attributesListPart.stream().filter(t -> t.getName().equals(node.getName())).findFirst();
    return attribute.orElse(null);
  }

  ASTAttributeUsage resolveInBehaviour(ASTNameExpression node, SysMLv2Scope scope) {
    var parent = scope.getAstNode();
    if(parent instanceof ASTSysMLModel || parent instanceof ASTSysMLPackage)
      return null;

    if(scope.resolveAttributeUsageDown(node.getName()).isPresent())
      return scope.resolveAttributeUsageDown(node.getName()).get().getAstNode();
    var attributesListPart = attributeResolveUtils.getAttributesOfElement((ASTSysMLElement) parent);
    var attribute = attributesListPart.stream().filter(t -> t.getName().equals(node.getName())).findFirst();
    if(attribute.isPresent())
      return attribute.get();
    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      return resolveInParts(node);
    }
    return resolveInBehaviour(node, (SysMLv2Scope) parent.getEnclosingScope());
  }

}
