package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;

public class PortsVisitor implements SysMLPartsVisitor2 {

  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();

  @Override
  public void visit(ASTPortUsage node) {
    var attributes = attributeResolveUtils.getAttributesOfElement(node);
    var value = attributes.stream().filter(t -> t.getName().equals("value")).findFirst();
    var delayed = attributes.stream().filter(t -> t.getName().equals("delayed")).findFirst();
    if(value.isPresent())
      node.setValueAttribute(value.get());
    if(delayed.isPresent())
      node.setDelayedAttribute(delayed.get());
  }

  @Override
  public void visit(ASTPortDef node) {
    var attributes = attributeResolveUtils.getAttributesOfElement(node);
    var value = attributes.stream().filter(t -> t.getName().equals("value")).findFirst();
    var delayed = attributes.stream().filter(t -> t.getName().equals("delayed")).findFirst();
    if(value.isPresent())
      node.setValueAttribute(value.get());
    if(delayed.isPresent())
      node.setDelayedAttribute(delayed.get());
  }

}
