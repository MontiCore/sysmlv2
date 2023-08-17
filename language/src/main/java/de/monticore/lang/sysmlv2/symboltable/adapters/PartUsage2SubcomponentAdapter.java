package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.componentconnector._ast.ASTParameterValue;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.KindOfComponent;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class PartUsage2SubcomponentAdapter extends MildInstanceSymbol {

  protected PartUsageSymbol adaptee;

  public PartUsage2SubcomponentAdapter(PartUsageSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  public PartUsageSymbol getAdaptee() {
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
  public CompKindExpression getType() {
    if(adaptee.getPartDef().isPresent()) {
      var compSymbol = new PartDef2ComponentAdapter(adaptee.getPartDef().get());
      return new KindOfComponent(compSymbol);
    }
    Log.error("0xMPf002 No type could be determined", getSourcePosition());
    return null;
  }

  @Override
  public List<ASTParameterValue> getParameterValuesList() {
    return getAdaptee().getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(a -> a.getDirection().equals(ASTSysMLFeatureDirection.FINAL))
        .map(a -> new ParmeterValueWrapper(a, getAdaptee()))
        .collect(Collectors.toList());
  }

}
