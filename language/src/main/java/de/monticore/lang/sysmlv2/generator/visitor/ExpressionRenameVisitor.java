package de.monticore.lang.sysmlv2.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PortResolveUtils;

import java.util.List;

public class ExpressionRenameVisitor implements ExpressionsBasisVisitor2 {
  public ExpressionRenameVisitor(ASTAttributeUsage attribute, String newName,
                                 List<ASTAttributeUsage> parentAttributeList) {
    this.attribute = attribute;
    this.newName = newName;
    this.parentAttributeList = parentAttributeList;
  }

  public ExpressionRenameVisitor(List<ASTAttributeUsage> parentAttributeList) {
    this.parentAttributeList = parentAttributeList;
  }

  public ExpressionRenameVisitor(List<ASTAttributeUsage> parentAttributeList, List<ASTPortUsage> parentPortList) {
    this.parentAttributeList = parentAttributeList;
    this.parentPortList = parentPortList;
  }

  public ASTAttributeUsage getAttribute() {
    return attribute;
  }

  public void setAttribute(ASTAttributeUsage attribute) {
    this.attribute = attribute;
  }

  public String getNewName() {
    return newName;
  }

  public void setNewName(String newName) {
    this.newName = newName;
  }

  ASTAttributeUsage attribute;

  String newName = "";

  List<ASTAttributeUsage> parentAttributeList;

  List<ASTPortUsage> parentPortList;

  @Override
  public void visit(ASTNameExpression nameExpression) {
    if(nameExpression.getName().endsWith(".value")) {
      String newName = nameExpression.getName().substring(0, nameExpression.getName().lastIndexOf(".value"));
      ASTNameExpression astNameExpression = new ASTNameExpression();
      astNameExpression.setEnclosingScope(nameExpression.getEnclosingScope());
      astNameExpression.setName(newName);
      var portUsage = resolvePortInBehaviour(astNameExpression, (SysMLv2Scope) astNameExpression.getEnclosingScope());
      if(parentPortList.contains(portUsage)) {
        nameExpression.setName("this.getParentPart()." + astNameExpression.getName()+ ".getValue()");
      }
    }

    var attributeUsage = resolveAttributeInBehaviour(nameExpression, (SysMLv2Scope) nameExpression.getEnclosingScope());
    if(parentAttributeList.contains(attributeUsage)) {
      nameExpression.setName("this.getParentPart()." + nameExpression.getName());
    }
    if(attribute != null) {
      if(attribute.equalAttributes(attributeUsage)) {
        nameExpression.setName(newName);
      }
    }
  }

  ASTAttributeUsage resolveAttributeInParts(ASTNameExpression node) {
    var scope = (SysMLv2Scope) node.getEnclosingScope();
    if(scope.resolveAttributeUsageDown(node.getName()).isPresent())
      return scope.resolveAttributeUsageDown(node.getName()).get().getAstNode();
    var attributesListPart = AttributeResolveUtils.getAttributesOfElement((ASTSysMLElement) scope.getAstNode());
    var attribute = attributesListPart.stream().filter(t -> t.getName().equals(node.getName())).findFirst();
    return attribute.orElse(null);
  }

  ASTAttributeUsage resolveAttributeInBehaviour(ASTNameExpression node, SysMLv2Scope scope) {
    var parent = scope.getAstNode();
    if(parent instanceof ASTSysMLModel || parent instanceof ASTSysMLPackage)
      return null;

    if(scope.resolveAttributeUsageDown(node.getName()).isPresent())
      return scope.resolveAttributeUsageDown(node.getName()).get().getAstNode();
    var attributesListPart = AttributeResolveUtils.getAttributesOfElement((ASTSysMLElement) parent);
    var attribute = attributesListPart.stream().filter(t -> t.getName().equals(node.getName())).findFirst();
    if(attribute.isPresent())
      return attribute.get();
    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      return resolveAttributeInParts(node);
    }
    return resolveAttributeInBehaviour(node, (SysMLv2Scope) parent.getEnclosingScope());
  }

  ASTPortUsage resolvePortInParts(ASTNameExpression node) {
    var scope = (SysMLv2Scope) node.getEnclosingScope();
    if(scope.resolvePortUsageDown(node.getName()).isPresent())
      return scope.resolvePortUsageDown(node.getName()).get().getAstNode();
    var portsOfElement = PortResolveUtils.getPortsOfElement((ASTSysMLElement) scope.getAstNode());
    var portUsage = portsOfElement.stream().filter(t -> t.getName().equals(node.getName())).findFirst();
    return portUsage.orElse(null);
  }

  ASTPortUsage resolvePortInBehaviour(ASTNameExpression node, SysMLv2Scope scope) {
    var parent = scope.getAstNode();
    if(parent instanceof ASTSysMLModel || parent instanceof ASTSysMLPackage)
      return null;

    if(scope.resolvePortUsageDown(node.getName()).isPresent())
      return scope.resolvePortUsageDown(node.getName()).get().getAstNode();
    var portsOfElement = PortResolveUtils.getPortsOfElement((ASTSysMLElement) parent);
    var portUsage = portsOfElement.stream().filter(t -> t.getName().equals(node.getName())).findFirst();
    if(portUsage.isPresent())
      return portUsage.get();
    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      return resolvePortInParts(node);
    }
    return resolvePortInBehaviour(node, (SysMLv2Scope) parent.getEnclosingScope());
  }

}
