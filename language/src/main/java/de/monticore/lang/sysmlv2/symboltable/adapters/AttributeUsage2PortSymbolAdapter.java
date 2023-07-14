package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpression;

public class AttributeUsage2PortSymbolAdapter extends PortSymbol {

  protected AttributeUsageSymbol adaptee;

  protected Timing timing;

  protected boolean isStronglyCausal;

  /**
   * Use {@code name} to specifiy a unique name, typically "portUsage.attrUsage"
   */
  public AttributeUsage2PortSymbolAdapter(String name, AttributeUsageSymbol adaptee, boolean conjugated) {
    super(name);
    this.adaptee = adaptee;
    this.incoming = conjugated ^ adaptee.getDirection().equals(ASTSysMLFeatureDirection.IN);
    this.outgoing = !this.incoming;
    // TODO Timing aus Komponente frickeln, in der die PortUsage liegt in deren Definition das Attribut liegt
    this.timing = Timing.TIMED;
    // TODO s.o.
    this.isStronglyCausal = true;
  }

  @Override
  public SymTypeExpression getType() {
    // TODO CoCo?
    return adaptee.getTypes(0);
  }

  @Override
  public ICompSymbolsScope getEnclosingScope() {
    return (ICompSymbolsScope) adaptee.getEnclosingScope();
  }

  @Override
  public Timing getTiming() {
    return timing;
  }

  @Override
  public boolean isStronglyCausal() {
    return isStronglyCausal;
  }

  @Override
  public Boolean getStronglyCausal() {
    return isStronglyCausal;
  }

}
