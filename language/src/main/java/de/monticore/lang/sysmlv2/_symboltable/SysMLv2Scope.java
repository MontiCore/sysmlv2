package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SysMLv2Scope extends SysMLv2ScopeTOP {

  public SysMLv2Scope() {
    super();
  }

  public SysMLv2Scope(boolean shadowing) {
    super(shadowing);
  }

  @Override public List<VariableSymbol> resolveAdaptedVariableLocallyMany(boolean foundSymbols, String name,
      AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    return new ArrayList<>(resolvePortUsageAsVariableSymbol(name));
  }

  protected List<VariableSymbol> resolvePortUsageAsVariableSymbol(String name) {
    // We ignore modifiers and predicates for the moment
    var ports = resolvePortUsageLocallyMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);

    var adapters = new ArrayList<VariableSymbol>();
    for (PortUsageSymbol portUsage : ports) {

      // we could access the qualified type name more safely, but we omit it for the moment
      var typeName = portUsage.getAstNode().getSpecialization(0).getSuperTypes(0).printType(
          new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));

      var resolvedSymbols = resolveAdaptedTypeLocallyMany(false, typeName, AccessModifier.ALL_INCLUSION, x -> true);

      if(resolvedSymbols.size() == 1) {
        var resolved = resolvedSymbols.get(0);

        TypeSymbol typeSymbol = new TypeSymbolSurrogate(typeName);
        typeSymbol.setSpannedScope(resolved.getSpannedScope());
        typeSymbol.setEnclosingScope(resolved.getEnclosingScope());
        typeSymbol.setAccessModifier(resolved.getAccessModifier());
        typeSymbol.setFullName(resolved.getFullName());
        typeSymbol.setPackageName(resolved.getPackageName());

        var type = new SymTypeVariable(typeSymbol);

        // we omit to set the ASTNode
        // we could create an adapter class, but using this builder seems more handy for now
        var variable = BasicSymbolsMill.variableSymbolBuilder() //
            .setName(portUsage.getName()) //
            .setEnclosingScope(portUsage.getEnclosingScope()) //
            .setFullName(portUsage.getFullName()) //
            .setPackageName(portUsage.getPackageName()) //
            .setAccessModifier(portUsage.getAccessModifier()) //
            .setType(type) //
            .build();

        adapters.add(variable);
      }
    }

    return adapters;
  }

  @Override public List<TypeSymbol> resolveAdaptedTypeLocallyMany(boolean foundSymbols, String name,
      AccessModifier modifier, Predicate<TypeSymbol> predicate) {

    var res = new ArrayList<TypeSymbol>();
    res.addAll(resolvePortDefAsTypeSymbol(name));
    res.addAll(resolveAttributeUsageAsTypeSymbol(name));
    return res;
  }

  protected List<TypeSymbol> resolvePortDefAsTypeSymbol(String name) {
    var optPortDefinitions = resolvePortDef(name);

    if(optPortDefinitions.isPresent()) {
      var portDefinition = optPortDefinitions.get();

      var typeSymbol = new TypeSymbol(portDefinition.getName());

      typeSymbol.setSpannedScope(portDefinition.getSpannedScope());
      typeSymbol.setEnclosingScope(portDefinition.getEnclosingScope());
      typeSymbol.setAccessModifier(portDefinition.getAccessModifier());
      typeSymbol.setFullName(portDefinition.getFullName());
      typeSymbol.setPackageName(portDefinition.getPackageName());

      return Collections.singletonList(typeSymbol);
    }
    return Collections.emptyList();
  }

  protected List<TypeSymbol> resolveAttributeUsageAsTypeSymbol(String name) {
    var optRes = resolveAttributeUsage(name);
    if(optRes.isPresent()) {
      var res = optRes.get();
      var node = res.getAstNode();
      var type = node.getSpecializationList().get(0).getSuperTypes(0);

      var typeSymbol = new TypeSymbol(type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())));

      typeSymbol.setEnclosingScope(node.getEnclosingScope());
      typeSymbol.setAccessModifier(res.getAccessModifier());
      typeSymbol.setFullName(res.getFullName());
      typeSymbol.setPackageName(res.getPackageName());

      // facilitates resolving the type of the get-function
      // we will overwork this functionality later
      var spannedScope = new BasicSymbolsScope();
      typeSymbol.setSpannedScope(spannedScope);
      var funSymbol = new FunctionSymbol("get");

      var funSpanned = new BasicSymbolsScope();
      funSymbol.setSpannedScope(funSpanned);

      var parameter = new VariableSymbol("int");
      parameter.setType(new SymTypePrimitive(new TypeSymbol("int")));
      funSpanned.add(parameter);

      funSymbol.setType(new SymTypePrimitive(typeSymbol));
      spannedScope.add(funSymbol);

      return Collections.singletonList(typeSymbol);
    }

    return Collections.emptyList();
  }

}
