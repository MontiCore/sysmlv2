package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

public class AttributeUsage2PortSymbolAdapter extends MildPortSymbol {

  protected AttributeUsageSymbol adaptee;

  protected Timing timing;

  /**
   * Use {@code name} to specifiy a unique name, typically "portUsage.attrUsage"
   */
  public AttributeUsage2PortSymbolAdapter(
      AttributeUsageSymbol adaptee,
      PortUsageSymbol container,
      boolean incoming)
  {
    super(container.getName() + "." + adaptee.getName());
    this.adaptee = adaptee;
    this.incoming = incoming;
    this.outgoing = !this.incoming;
    // TODO Timing aus Komponente frickeln, in der die PortUsage liegt in deren Definition das Attribut liegt
    this.timing = Timing.TIMED;
    // TODO s.o.
    this.stronglyCausal = container.isStrong();

    // Das muss anscheinend gesetzt sein, weil die MC-Interna immer über das Feld herausfinden, ob sie getEnclosingScope
    // benutzen können (nicht null) und so Scheissereien wie determineFullName auch über das Feld laufen.
    // Vorsicht: Muss das Scope der PortUsage sein, denn der Port ist konzeptuell "in der PartDef", nicht "in der
    // PortDef" !!
    this.enclosingScope = (ICompSymbolsScope) container.getEnclosingScope();

    if(adaptee.getTypesList().size() == 1) {
      this.type = adaptee.getTypes(0);
    }
    else if(adaptee.getTypesList().size() > 1) {
      Log.warn("0x10015 Attr. usage has more than one type, adaptation to CC-Port defaulting to first type",
          adaptee.getSourcePosition());
      this.type = adaptee.getTypes(0);
    }
    else {
      Log.warn("0x10016 Attr. usage has no type, adaptation to CC-Port defaulting to obscure type",
          adaptee.getSourcePosition());
      this.type = SymTypeExpressionFactory.createObscureType();
    }
  }

  // Dieser Override dient nur dazu den Clusterfuck aus mehreren Feldern "stronglyCausal" in PortSymbol und
  // PortSymbolTOP zu "beheben".
  @Override
  public boolean isStronglyCausal() {
    return this.stronglyCausal;
  }

  // Dieser Override dient nur dazu den Clusterfuck aus mehreren Feldern "stronglyCausal" in PortSymbol und
  // PortSymbolTOP zu "beheben".
  @Override
  public Boolean getStronglyCausal() {
    return this.stronglyCausal;
  }

  @Override
  public Timing getTiming() {
    return timing;
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
