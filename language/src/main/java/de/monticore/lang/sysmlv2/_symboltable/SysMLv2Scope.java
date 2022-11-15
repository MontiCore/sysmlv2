package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolBuilder;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        var variable = BasicSymbolsMill.variableSymbolBuilder()
            .setName(portUsage.getName())
            .setEnclosingScope(portUsage.getEnclosingScope())
            .setFullName(portUsage.getFullName())
            .setPackageName(portUsage.getPackageName())
            .setAccessModifier(portUsage.getAccessModifier())
            .setType(new SymTypeOfObject(resolved))
            .build();

        adapted.add(variable);
      }
    }

    for (AttributeUsageSymbol attrUsage : attributes) {
      var types = attrUsage.getTypesList();

      if(types.size() == 1) {
        // resolve the globally defined generic Stream-type
        var streamType = SysMLv2Mill.globalScope().resolveType("Stream");
        var attributeType = types.get(0);

        if(streamType.isEmpty()) {
          Log.error("Stream not defined in global scope. Initialize it with 'SysMLv2Mill.addStreamType()'!");
          continue;
        }

        // set concrete type to generic stream
        var streamOfAttrType = SymTypeExpressionFactory.createGenerics(streamType.get(), attributeType);

        // we omit to set the ASTNode
        var variable = BasicSymbolsMill.variableSymbolBuilder()
            .setName(attrUsage.getName())
            .setEnclosingScope(attrUsage.getEnclosingScope())
            .setFullName(attrUsage.getFullName())
            .setPackageName(attrUsage.getPackageName())
            .setAccessModifier(attrUsage.getAccessModifier())
            .setType(streamOfAttrType)
            .build();

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
    var portDef = resolvePortDef(name);
    if(portDef.isPresent()) {
      // baue neues TypeSymbol
      adapted.add(
          typeSymbolBuilder(portDef.get(), portDef.get().getSpannedScope())
              .build()
      );
    }

    // PartDefs zu Types
    var partDef = resolvePartDef(name);
    if(partDef.isPresent()) {
      // baue neues TypeSymbol
      adapted.add(
          typeSymbolBuilder(partDef.get(), partDef.get().getSpannedScope())
              .build()
      );
    }

    // StateDef zu Types
    var stateDef = resolveStateDef(name);
    if(stateDef.isPresent()) {
      // baue neues TypeSymbol
      adapted.add(
          typeSymbolBuilder(stateDef.get(), stateDef.get().getSpannedScope())
              .build()
      );
    }

    return adapted;
  }

  private TypeSymbolBuilder typeSymbolBuilder(SysMLTypeSymbol symbol, IBasicSymbolsScope spannedScope) {
    // verhindert NullPointers im Typechecker
    Objects.requireNonNull(spannedScope);

    return BasicSymbolsMill.typeSymbolBuilder()
        .setName(symbol.getName())
        .setPackageName(symbol.getPackageName())
        .setFullName(symbol.getFullName())
        .setAccessModifier(symbol.getAccessModifier())
        .setEnclosingScope(symbol.getEnclosingScope())
        .setSpannedScope(spannedScope);
  }

}
