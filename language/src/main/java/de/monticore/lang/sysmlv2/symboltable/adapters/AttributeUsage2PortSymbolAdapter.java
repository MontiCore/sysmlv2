package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpression;

public class AttributeUsage2PortSymbolAdapter extends MildPortSymbol {

  protected AttributeUsageSymbol adaptee;

  protected Timing timing;

  protected boolean isStronglyCausal;

  /**
   * Use {@code name} to specifiy a unique name, typically "portUsage.attrUsage"
   */
  public AttributeUsage2PortSymbolAdapter(PortUsageSymbol container, AttributeUsageSymbol adaptee) {
    super(container.getName() + "." + adaptee.getName());
    this.adaptee = adaptee;
    // TODO Annahme: entweder Conjugated oder nicht
    this.incoming = !container.isEmptyConjugatedTypes() ^ adaptee.getDirection().equals(ASTSysMLFeatureDirection.IN);
    this.outgoing = !this.incoming;
    // TODO Timing aus Komponente frickeln, in der die PortUsage liegt in deren Definition das Attribut liegt
    this.timing = Timing.TIMED;
    // TODO s.o.
    this.isStronglyCausal = true;

    // Das muss anscheinend gesetzt sein, weil die MC-Interna immer über das Feld herausfinden, ob sie getEnclosingScope
    // benutzen können (nicht null) und so Scheissereien wie determineFullName auch über das Feld laufen.
    // Vorsicht: Muss das Scope der PortUsage sein, denn der Port ist konzeptuell "in der PartDef", nicht "in der
    // PortDef" !!
    this.enclosingScope = (ICompSymbolsScope) container.getEnclosingScope();
  }

  @Override
  public SymTypeExpression getType() {
    // TODO CoCo?
    return adaptee.getTypes(0);
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

  @Override
  public boolean equals(Object other) {
    if(other instanceof AttributeUsage2PortSymbolAdapter) {
      return this.adaptee.equals(((AttributeUsage2PortSymbolAdapter) other).adaptee);
    }
    else {
      return super.equals(other);
    }
  }

}
