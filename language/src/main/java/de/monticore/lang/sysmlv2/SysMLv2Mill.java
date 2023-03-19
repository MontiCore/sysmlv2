/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SysMLv2Mill extends SysMLv2MillTOP {

  /**
   * Wraps the {@link BasicSymbolsMill#initializePrimitives()} and adds the "nat" primitive.
   */
  public static void initializePrimitives() {
    var primitives = BasicSymbolsMill.PRIMITIVE_LIST;
    var boxMap = SymTypePrimitive.boxMap;
    var unboxMap = SymTypePrimitive.unboxMap;

    if(!primitives.contains("nat")) {
      primitives.add("nat");
    }

    if(!boxMap.containsKey("nat")) {
      boxMap.put("nat", "Natural");
    }

    if(!unboxMap.containsKey("Natural")) {
      unboxMap.put("Natural", "nat");
    }

    BasicSymbolsMill.initializePrimitives();
  }

  public static void addStringType() {
    getMill()._addStringType();
  }

  protected void _addStringType() {
    // ensures adding the type symbol only once
    if(SysMLv2Mill.globalScope().resolveType("String").isPresent()) {
      return;
    }

    var type =  OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("String")
        .setSpannedScope(new OOSymbolsScope())
        .build();

    SysMLv2Mill.globalScope().add(type);
  }


  /**
   * @see SysMLv2Mill#addStreamType()
   */
  public static void addStreamType() {
    getMill()._addStreamType();
  }

  /**
   * Adds the stream type and its functions to the global scope. Call this method after the mill's init method whenever
   * the stream type needs to be resolved. Requires initializing the primitive types
   * {@code BasicSymbolsMill.initializePrimitives();}.
   *
   * @author Marc Schmidt
   */
  protected void _addStreamType() {
    // ensures adding the type symbol only once
    if(SysMLv2Mill.globalScope().resolveType("Stream").isPresent()) {
      return;
    }

    // Stream<E> bauen und ins GS hinzufügen
    SysMLv2Mill.globalScope().add(buildStreamType());
  }

  protected OOTypeSymbol buildStreamType() {
    var typeVar = BasicSymbolsMill.typeVarSymbolBuilder().setName("E").build();

    var spannedScope = new OOSymbolsScope();
    spannedScope.add(typeVar);
    spannedScope.add(buildSnthFunction(typeVar));
    spannedScope.add(buildLengthFunction());
    spannedScope.add(buildValuesFunction(typeVar));

    return OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Stream")
        .setSpannedScope(spannedScope)
        .build();
  }

  public static void addCollectionTypes() {
    getMill()._addCollectionTypes();
  }

  protected void _addCollectionTypes() {
    // ensures adding the type symbol only once
    if(SysMLv2Mill.globalScope().resolveType("List").isEmpty()) {
      // TODO add collection function such as size
      SysMLv2Mill.globalScope().add(buildCollectionType("List", "A"));
    }

    if(SysMLv2Mill.globalScope().resolveType("Optional").isEmpty()) {
      SysMLv2Mill.globalScope().add(buildCollectionType("Optional", "A"));
    }

    if(SysMLv2Mill.globalScope().resolveType("Set").isEmpty()) {
      SysMLv2Mill.globalScope().add(buildCollectionType("Set", "A"));
    }

    if(SysMLv2Mill.globalScope().resolveType("Map").isEmpty()) {
      SysMLv2Mill.globalScope().add(buildCollectionType("Map", "A", "B"));
    }
  }

  protected OOTypeSymbol buildCollectionType(String name, String ... typeVars) {
    var spannedScope = new OOSymbolsScope();

    Arrays
        .stream(typeVars)
        .map(typeVarName -> BasicSymbolsMill.typeVarSymbolBuilder().setName(typeVarName).build())
        .forEach(spannedScope::add);

    return OOSymbolsMill.oOTypeSymbolBuilder()
        .setName(name)
        .setSpannedScope(spannedScope)
        .build();
  }

  protected SymTypePrimitive buildIntType() {
    return SymTypeExpressionFactory.createPrimitive(SysMLv2Mill.globalScope().resolveType("int").get());
  }

  protected FunctionSymbol buildSnthFunction(TypeVarSymbol typeVar) {
    var parameterList = new BasicSymbolsScope();

    VariableSymbol parameter = SysMLv2Mill.variableSymbolBuilder().setName("n").setType(buildIntType()).build();
    parameterList.add(typeVar);
    parameterList.add(parameter);

    SymTypeVariable returnType = SymTypeExpressionFactory.createTypeVariable(typeVar);

    return BasicSymbolsMill.functionSymbolBuilder()
        .setName("snth")
        .setSpannedScope(parameterList)
        .setType(returnType)
        .build();
  }

  protected FunctionSymbol buildLengthFunction() {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("length")
        .setType(buildIntType())
        .setSpannedScope(new BasicSymbolsScope())
        .build();
  }

  protected FunctionSymbol buildValuesFunction(TypeVarSymbol typeVar) {
    var listSymbol = SysMLv2Mill.globalScope().resolveType("Set").get();
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("values")
        .setType(SymTypeExpressionFactory.createGenerics(
            listSymbol,
            SymTypeExpressionFactory.createTypeVariable(typeVar))
        )
        .setSpannedScope(new BasicSymbolsScope())
        .build();
  }
}
