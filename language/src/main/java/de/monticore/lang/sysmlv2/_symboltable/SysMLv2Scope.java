/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.EnumDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlstates.symboltable.adapters.StateDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.PartDef2ComponentAdapter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("OptionalIsPresent") public class SysMLv2Scope extends SysMLv2ScopeTOP {

  public SysMLv2Scope() {
    super();
  }

  public SysMLv2Scope(boolean shadowing) {
    super(shadowing);
  }

  /**
   * Adaptiert AttributeUsages oder PortUsages zu Variablen.
   * <br>
   * Wird für den TypeCheck benötigt, da wir in der Grammatik uns dafür entschieden haben, die SysML-Konzepte nicht von
   * Type, Field, etc. abzuleiten. Das ist konsistent mit den Empfehlungen aus dem MC-Buch.
   *
   * @author: Marc Schmidt, Mathias Pfeiffer
   */
  @Override
  public List<VariableSymbol> resolveAdaptedVariableLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<VariableSymbol> predicate
  ) {
    // We ignore modifiers and predicates for the moment
    var ports = resolvePortUsageLocallyMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);
    var attributes = resolveAttributeUsageLocallyMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);

    var adapted = new ArrayList<VariableSymbol>();
    for (PortUsageSymbol portUsage : ports) {
      var types = new ArrayList<SymTypeExpression>();
      types.addAll(portUsage.getTypesList());
      types.addAll(portUsage.getConjugatedTypesList());

      if(types.size() == 1) {
        var resolved = types.get(0).getTypeInfo();

        // we omit to set the ASTNode
        var variable = new PortUsage2VariableSymbolAdapter(portUsage);

        if(portUsage.getAstNode().getCardinality().isPresent()) {
          variable.setType(SymTypeExpressionFactory.createTypeArray(resolved, 1,
              SymTypeExpressionFactory.createTypeObject(resolved)));
        }else {
          variable.setType(SymTypeExpressionFactory.createTypeObject(resolved));
        }

        adapted.add(variable);
      }
    }

    for (AttributeUsageSymbol attrUsage : attributes) {
      var types = attrUsage.getTypesList();

      if(types.size() == 1) {
        var attributeType = types.get(0);

        // we omit to set the ASTNode
        var variable = new AttributeUsage2VariableSymbolAdapter(attrUsage);

        if(attrUsage.getAstNode().getCardinality().isPresent()) {
          variable.setType(SymTypeExpressionFactory.createTypeArray(attributeType.getTypeInfo(), 1,
              attributeType));
        }else {
          variable.setType(attributeType);
        }

        adapted.add(variable);
      }
    }

    // Falls Requirement, dann subject befragen
    if(this.astNode.isPresent() && this.astNode.get() instanceof ASTRequirementUsage) {
      var ast = (ASTRequirementUsage) this.astNode.get();
      if(ast.isPresentRequirementSubject()) {
        // Alle Typen kommen in Frage
        ast.getRequirementSubject().getSpecializationList().stream().flatMap(ASTSpecialization::streamSuperTypes).forEach(t -> {
          var typeSymbol = t.getDefiningSymbol();
          if(typeSymbol.isPresent() && typeSymbol.get() instanceof IScopeSpanningSymbol) {
            var scope = ((IScopeSpanningSymbol)typeSymbol.get()).getSpannedScope();
            if(scope instanceof IBasicSymbolsScope) {
              var variable = ((IBasicSymbolsScope) scope).resolveVariableDown(name, modifier, predicate);
              if(variable.isPresent()) {
                adapted.add(variable.get());
              }
            }
          }
        });
      }
    }

    return adapted;
  }

  /**
   * Adaptiert SysML Definitions (PortDefs, PartDefs, StateDef) zu Types. Dient dazu, dass man an verschiedenen Stellen
   * nach "Type" resolven kann (scope.resolveType('MyPortDef') zB.) und ein (adaptiertes) Type-Symbol findet. Eingesetzt
   * für TypeCheck.
   *
   * @author Marc Schmidt
   */
  @Override
  public List<TypeSymbol> resolveAdaptedTypeLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<TypeSymbol> predicate
  ) {
    var adapted = new ArrayList<TypeSymbol>();

    // Adaptiert PortDefs zu Types, dh. wenn wir für "port myPort: MyPortDef;" nach "MyPortDef" suchen, dann können wir
    // das mit "scope.resolveType('MyPortDef')" tun!
    var portDef = resolvePortDefLocally(name);
    if(portDef.isPresent()) {
      adapted.add(new PortDef2TypeSymbolAdapter(portDef.get()));
    }

    // PartDefs zu Types
    var partDef = resolvePartDefLocally(name);
    if(partDef.isPresent()) {
      adapted.add(new PartDef2TypeSymbolAdapter(partDef.get()));
    }

    // StateDef zu Types
    var stateDef = resolveStateDefLocally(name);
    if(stateDef.isPresent()) {
      adapted.add(new StateDef2TypeSymbolAdapter(stateDef.get()));
    }

    // AttributeDef zu Types
    var attributeDef = resolveAttributeDefLocally(name);
    if(attributeDef.isPresent()) {
      adapted.add(new AttributeDef2TypeSymbolAdapter(attributeDef.get()));
    }

    // EnumDef zu Types
    var enumDef = resolveEnumDefLocally(name);
    if(enumDef.isPresent()) {
      adapted.add(new EnumDef2TypeSymbolAdapter(enumDef.get()));
    }

    return adapted;
  }

  @Override
  public List<ComponentSymbol> resolveAdaptedComponentLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<ComponentSymbol> predicate
  ) {
    var adapted = new ArrayList<ComponentSymbol>();

    var partDef = resolvePartDefLocally(name);
    if(partDef.isPresent()) {
      adapted.add(new PartDef2ComponentAdapter(partDef.get()));
    }

    return adapted;
  }

}
