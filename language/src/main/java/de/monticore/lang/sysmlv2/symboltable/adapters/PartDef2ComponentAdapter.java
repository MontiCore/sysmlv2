package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
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

 /* @Override
  public List<MildPortSymbol> getPorts() {
    return getAdaptee().getSpannedScope().getLocalPortUsageSymbols().stream()
        .filter(pu -> pu.getTypes(0).getTypeInfo() instanceof PortDef2TypeSymbolAdapter)
        .map(pu -> Pair.of(pu, ((PortDef2TypeSymbolAdapter)pu.getTypes(0).getTypeInfo()).))
        .map(pair -> new )
  }*/

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
