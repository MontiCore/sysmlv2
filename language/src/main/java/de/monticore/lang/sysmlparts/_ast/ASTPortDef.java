package de.monticore.lang.sysmlparts._ast;

public class ASTPortDef extends ASTPortDefTOP{

  public ASTAttributeUsage getValueAttribute() {
    return valueAttribute;
  }

  public void setValueAttribute(ASTAttributeUsage valueAttribute) {
    this.valueAttribute = valueAttribute;
  }

  ASTAttributeUsage valueAttribute;

  public ASTAttributeUsage getDelayedAttribute() {
    return delayedAttribute;
  }

  public void setDelayedAttribute(ASTAttributeUsage delayedAttribute) {
    this.delayedAttribute = delayedAttribute;
  }

  ASTAttributeUsage delayedAttribute;

  public boolean isPresentDelayed(){ return delayedAttribute != null;}
}
