package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.componentconnector.ComponentConnectorMill;
import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.KindOfComponent;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public ISysMLv2Scope getEnclosingScope() {
    return (ISysMLv2Scope) getAdaptee().getEnclosingScope();
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

  @Override
  public List<PortSymbol> getPorts() {
    return getAdaptee().getSpannedScope().getLocalPortUsageSymbols().stream()
        // (PortUsage x List(AttrUsage)) um eindeutigen Namen bilden zu können & Richtung zu kennen
        .map(pu -> Pair.of(pu, pu.getInnerAttributes())) // TODO Könnten Conjugated sein...
        .flatMap(pair -> pair.getRight().stream()
            .map(au -> new AttributeUsage2PortSymbolAdapter(pair.getLeft(), au))
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
    return getSpannedScope().getLocalPartUsageSymbols().stream()
        .map(PartUsage2SubcomponentAdapter::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<CompKindExpression> getRefinementsList() {
    List<CompKindExpression> res = new ArrayList<>();
    for(var partDefSymbol: adaptee.getDirectRefinements()) {
      var compSymbol = ((ISysMLv2Scope)partDefSymbol.getEnclosingScope()).resolveComponent(partDefSymbol.getName());
      if(compSymbol.isPresent()) {
        res.add(new KindOfComponent(compSymbol.get()));
      }
    }
    return res;
  }

  @Override
  public  List<ASTConnector> getConnectorsList() {
    if(adaptee.isPresentAstNode()) {
      var sysmlConnectors = adaptee.getAstNode().getSysMLElements(ASTConnectionUsage.class);
      return sysmlConnectors.stream().map(ConnectorWrapper::new).collect(Collectors.toList());
    }
    else {
      Log.warn("0xMPf004 Attempted to get connectors for a symbol");
      return new ArrayList<>();
    }
  }

}
