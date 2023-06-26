package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.componentconnector.ComponentConnectorMill;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbolBuilder;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PartDef2ComponentAdapter extends MildComponentSymbol {

  protected PartDefSymbol adaptee;

  public PartDef2ComponentAdapter(PartDefSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  public PartDefSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public ISysMLv2Scope getSpannedScope() {
    return (ISysMLv2Scope) getAdaptee().getSpannedScope();
  }

  @Override
  public List<VariableSymbol> getParameters() {
    return getAdaptee().getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(a -> a.getDirection().equals(ASTSysMLFeatureDirection.FINAL))
        .map(a -> new AttributeUsage2VariableSymbolAdapter(a))
        .collect(Collectors.toList());
  }

  /**
   * Adapter wegen Scopes - die müssen auf die echten Scopes des Originals verweisen
   */
  class PortSymbolAdapter extends PortSymbol {

    protected AttributeUsageSymbol adaptee;

    protected PortSymbolAdapter(String name) {
      super(name);
    }

    /**
     * Use {@code name} to specifiy a unique name, typically "portUsage.attrUsage"
     */
    public PortSymbolAdapter(String name, AttributeUsageSymbol adaptee, boolean conjugated) {
      super(name);
      this.adaptee = adaptee;
      this.incoming = conjugated ^ adaptee.getDirection().equals(ASTSysMLFeatureDirection.IN);
      this.outgoing = !this.incoming;
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
  }

  @Override
  public List<PortSymbol> getPorts() {
    return getAdaptee().getSpannedScope().getLocalPortUsageSymbols().stream()
        // Alle PortUsages, deren Type eine PortDef ist
        .filter(pu -> pu.getTypes(0).getTypeInfo() instanceof PortDef2TypeSymbolAdapter)
        // (PortUsage x List(AttrUsage)) um eindeutigen Namen bilden zu können & Richtung zu kennen
        .map(pu -> Pair.of(pu, ((ISysMLv2Scope)pu.getTypes(0).getTypeInfo().getSpannedScope()).getLocalAttributeUsageSymbols()))
        .flatMap(pair -> pair.getRight().stream()
            .map(au -> new PortSymbolAdapter(
                pair.getLeft().getName() + "." + au.getName(),
                au,
                !pair.getLeft().isEmptyConjugatedTypes()))
        )
        .collect(Collectors.toList());
  }

  @Override
  public void addParameter(@NonNull VariableSymbol parameter) {
    Log.error("Should not be called");
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return getSpannedScope().getLocalTypeVarSymbols();
  }

  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    return getSpannedScope().getLocalSubcomponentSymbols();
  }

}
