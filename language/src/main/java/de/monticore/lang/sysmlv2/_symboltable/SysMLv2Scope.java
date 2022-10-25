package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirements._symboltable.ISysMLRequirementsScope;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypePrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SysMLv2Scope extends SysMLv2ScopeTOP {

  public SysMLv2Scope() {
    super();
  }

  public SysMLv2Scope(boolean shadowing) {
    super(shadowing);
  }

  /**
   * Adaptiert Variablen (oder Fields, sind ein Super- oder Subtyp davon) auf AttributeUsages oder PortUsages. Wird für
   * den TypeCheck benötigt, da wir in der Grammatik uns dafür entschieden haben, die SysML-Konzepte nicht von Type,
   * Field, etc. abzuleiten. Das ist konsistent mit den Empfehlungen aus dem MC-Buch.
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

    var res = new ArrayList<VariableSymbol>();
    for (PortUsageSymbol portUsage : ports) {
      var resolvedSymbols = new ArrayList<SymTypeExpression>();
      resolvedSymbols.addAll(portUsage.getTypesList());
      resolvedSymbols.addAll(portUsage.getConjugatedTypesList());

      if(resolvedSymbols.size() == 1) {
        var resolved = resolvedSymbols.get(0).getTypeInfo();

        TypeSymbol typeSymbol = new TypeSymbolSurrogate(resolved.getName());
        typeSymbol.setSpannedScope(resolved.getSpannedScope());
        typeSymbol.setEnclosingScope(resolved.getEnclosingScope());
        typeSymbol.setAccessModifier(resolved.getAccessModifier());
        typeSymbol.setFullName(resolved.getFullName());
        typeSymbol.setPackageName(resolved.getPackageName());

        // we omit to set the ASTNode
        // we could create an adapter class, but using this builder seems more handy for now
        var variable = BasicSymbolsMill.variableSymbolBuilder()
            .setName(portUsage.getName())
            .setEnclosingScope(portUsage.getEnclosingScope())
            .setFullName(portUsage.getFullName())
            .setPackageName(portUsage.getPackageName())
            .setAccessModifier(portUsage.getAccessModifier())
            .setType(new SymTypeOfObject(typeSymbol))
            .build();

        res.add(variable);
      }
    }

    for (AttributeUsageSymbol attrUsage : attributes) {
      var resolvedSymbols = attrUsage.getTypesList();

      if(resolvedSymbols.size() == 1) {
        var resolved = resolvedSymbols.get(0).getTypeInfo();

        TypeSymbol typeSymbol = new TypeSymbolSurrogate(resolved.getName());
        typeSymbol.setSpannedScope(resolved.getSpannedScope());
        typeSymbol.setEnclosingScope(resolved.getEnclosingScope());
        typeSymbol.setAccessModifier(resolved.getAccessModifier());
        typeSymbol.setFullName(resolved.getFullName());
        typeSymbol.setPackageName(resolved.getPackageName());

        // we omit to set the ASTNode
        // we could create an adapter class, but using this builder seems more handy for now
        var variable = BasicSymbolsMill.variableSymbolBuilder()
            .setName(attrUsage.getName())
            .setEnclosingScope(attrUsage.getEnclosingScope())
            .setFullName(attrUsage.getFullName())
            .setPackageName(attrUsage.getPackageName())
            .setAccessModifier(attrUsage.getAccessModifier())
            .setType(new SymTypePrimitive(typeSymbol)) // TODO Kann auch Object, Enum... sein
            .build();

        res.add(variable);
      }
    }

    // Falls Requirement, dann subject befragen
    if(this.astNode.isPresent() && this.astNode.get() instanceof ASTRequirementUsage) {
      var ast = (ASTRequirementUsage) this.astNode.get();
      if(ast.isPresentRequirementSubject()) {
        // Alle Typen kommen in Frage
        ast.getRequirementSubject().getSpecializationList().stream().flatMap(s -> s.streamSuperTypes()).forEach(t -> {
          var type = t.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
          var typeSymbol = t.getDefiningSymbol();
          if(typeSymbol.isPresent() && typeSymbol.get() instanceof IScopeSpanningSymbol) {
            var scope = ((IScopeSpanningSymbol)typeSymbol.get()).getSpannedScope();
            if(scope instanceof IBasicSymbolsScope) {
              var variable = ((IBasicSymbolsScope) scope).resolveVariableDown(name, modifier, predicate);
              if(variable.isPresent()) {
                res.add(variable.get());
              }
            }
          }
        });
      }
    }

    return res;
  }


  /**
   * Adaptiert SysML Definitions (PortDefs) zu Types. Dient dazu, dass man an verschiedenen Stellen nach
   * "Type" resolven kann (scope.resolveType('MyPortDef') zB.) und ein (adaptiertes) Type-Symbol findet. Eingesetzt für
   * TypeCheck.
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
    var res = new ArrayList<TypeSymbol>();

    // Adaptiert PortDefs zu Types, dh. wenn wir für "port myPort: MyPortDef;" nach "MyPortDef" suchen, dann können wir
    // das mit "scope.resolveType('MyPortDef')" tun!
    var portDef = resolvePortDef(name);
    if(portDef.isPresent()) {
      // baue neues TypeSymbol
      res.add(
          SysMLv2Mill.typeSymbolBuilder()
              .setName(portDef.get().getName())
              .setSpannedScope(portDef.get().getSpannedScope())
              .setEnclosingScope(portDef.get().getEnclosingScope())
              .setAccessModifier(portDef.get().getAccessModifier())
              .setFullName(portDef.get().getFullName())
              .setPackageName(portDef.get().getPackageName())
              .build()
      );
    }

    // PartDefs zu Types
    var partDef = resolvePartDef(name);
    if(partDef.isPresent()) {
      // baue neues TypeSymbol
      res.add(
          SysMLv2Mill.typeSymbolBuilder()
              .setName(partDef.get().getName())
              .setSpannedScope(partDef.get().getSpannedScope())
              .setEnclosingScope(partDef.get().getEnclosingScope())
              .setAccessModifier(partDef.get().getAccessModifier())
              .setFullName(partDef.get().getFullName())
              .setPackageName(partDef.get().getPackageName())
              .build()
      );
    }

    // StateDef zu Types
    var stateDef = resolveStateDef(name);
    if(stateDef.isPresent()) {
      // baue neues TypeSymbol
      res.add(
          SysMLv2Mill.typeSymbolBuilder()
              .setName(stateDef.get().getName())
              .setSpannedScope(stateDef.get().getSpannedScope())
              .setEnclosingScope(stateDef.get().getEnclosingScope())
              .setAccessModifier(stateDef.get().getAccessModifier())
              .setFullName(stateDef.get().getFullName())
              .setPackageName(stateDef.get().getPackageName())
              .build()
      );
    }

    return res;
  }

}
