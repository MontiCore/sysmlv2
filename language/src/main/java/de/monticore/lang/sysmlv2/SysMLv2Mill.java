/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;

import java.util.Arrays;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMillTOP.getMill;

public class SysMLv2Mill extends SysMLv2MillTOP {

  /**
   * Prepares the current global scope, i.e., adds symbols to it. Does not initialize anything else, especially not the
   * Mill itself. Does not clear the scope beforehand.
   */
  public static void prepareGlobalScope() {
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addStringType();
    // Doppelt gemoppelt?
    MCCollectionSymTypeRelations.init();
    SysMLv2Mill.addCollectionTypes();
    OCLSymTypeRelations.init();
  }

  /**
   * BasicSymbolsMill.initializePrimitives plus our own
   */
  public static void initializePrimitives() {
    BasicSymbolsMill.initializePrimitives();
    getMill()._initializePrimitives();
  }

  /**
   * Adds "nat" type
   */
  protected void _initializePrimitives() {
    IBasicSymbolsGlobalScope gs = globalScope();
    gs.add(typeSymbolBuilder()
        .setName("nat")
        .setEnclosingScope(globalScope())
        .setFullName("nat")
        .setSpannedScope(scope())
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build());
  }

  public static void addStringType() {
    getMill()._addStringType();
  }

  protected void _addStringType() {
    // ensures adding the type symbol only once
    if (SysMLv2Mill.globalScope().resolveType("String").isPresent()) {
      return;
    }

    var type = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("String")
        .setSpannedScope(new OOSymbolsScope())
        .build();

    SysMLv2Mill.globalScope().add(type);
  }

  protected OOTypeSymbol buildOptionalType() {
    var typeVar = BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build();

    var spannedScope = new OOSymbolsScope();
    spannedScope.add(typeVar);

    spannedScope.add(
        SysMLv2Mill.functionSymbolBuilder()
            .setName("get")
            .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
            .setSpannedScope(new BasicSymbolsScope())
            .build()
    );

    return OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Optional")
        .setSpannedScope(spannedScope)
        .build();
  }

  /**
   * @see SysMLv2Mill#addStreamType()
   */
  public static void addStreamType() {
    getMill()._addStreamType();
  }

  /**
   * Adds the stream type and its functions to the global scope. Call this
   * method after the mill's init method whenever the stream type needs to be
   * resolved. Requires initializing the primitive types
   * {@code BasicSymbolsMill.initializePrimitives();}.
   */
  protected void _addStreamType() {
    // ensures adding the type symbol only once
    if (SysMLv2Mill.globalScope().resolveType("Stream").isPresent()) {
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

    var res = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Stream")
        .setSpannedScope(spannedScope)
        .build();

    // Stream muss bereits existieren
    spannedScope.add(buildAtTimeFunction(res, typeVar));
    spannedScope.add(buildMessagesFunction(res, typeVar));
    spannedScope.add(buildTimesFunction(res, typeVar));
    spannedScope.add(buildInfTimesFunction(res, typeVar));
    spannedScope.add(buildTakesFunction(res, typeVar));

    return res;
  }

  public static void addCollectionTypes() {
    getMill()._addCollectionTypes();
  }

  protected void _addCollectionTypes() {
    // ensures adding the type symbol only once
    if (SysMLv2Mill.globalScope().resolveType("List").isEmpty()) {
      var list = buildCollectionType("List", "A");
      var typeVar = list.getSpannedScope().getTypeVarSymbols().get("A").get(0);
      list.getSpannedScope().add(buildCountFunction());
      list.getSpannedScope().add(buildHeadFunction(typeVar));
      list.getSpannedScope().add(buildTailFunction(list, typeVar));
      list.getSpannedScope().add(buildAppendFunction(list, typeVar));
      SysMLv2Mill.globalScope().add(list);
    }

    if (SysMLv2Mill.globalScope().resolveType("Optional").isEmpty()) {
     // SysMLv2Mill.globalScope().add(buildCollectionType("Optional", "A"));
      SysMLv2Mill.globalScope().add(buildOptionalType());
    }

    if (SysMLv2Mill.globalScope().resolveType("Set").isEmpty()) {
      SysMLv2Mill.globalScope().add(buildCollectionType("Set", "A"));
    }

    if (SysMLv2Mill.globalScope().resolveType("Map").isEmpty()) {
      SysMLv2Mill.globalScope().add(buildCollectionType("Map", "A", "B"));
    }
  }

  protected OOTypeSymbol buildCollectionType(String name, String... typeVars) {
    var spannedScope = new OOSymbolsScope();

    Arrays
        .stream(typeVars)
        .map(typeVarName -> BasicSymbolsMill.typeVarSymbolBuilder().setName(
            typeVarName).build())
        .forEach(spannedScope::add);

    return OOSymbolsMill.oOTypeSymbolBuilder()
        .setName(name)
        .setSpannedScope(spannedScope)
        .build();
  }

  protected SymTypePrimitive buildIntType() {
    return SymTypeExpressionFactory.createPrimitive(
        SysMLv2Mill.globalScope().resolveType("int").get());
  }

  protected SymTypePrimitive buildNatType() {
    return SymTypeExpressionFactory.createPrimitive(
        SysMLv2Mill.globalScope().resolveType("nat").get());
  }

  protected FunctionSymbol buildSnthFunction(TypeVarSymbol typeVar) {
    var parameterList = new BasicSymbolsScope();

    VariableSymbol parameter = SysMLv2Mill.variableSymbolBuilder().setName(
        "n").setType(buildIntType()).build();
    parameterList.add(typeVar);
    parameterList.add(parameter);

    SymTypeVariable returnType = SymTypeExpressionFactory.createTypeVariable(
        typeVar);

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

  protected FunctionSymbol buildCountFunction() {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("count")
        .setType(buildIntType())
        .setSpannedScope(new BasicSymbolsScope())
        .build();
  }

  protected FunctionSymbol buildHeadFunction(TypeVarSymbol typeVar) {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("head")
        .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
        .setSpannedScope(new BasicSymbolsScope())
        .build();
  }

  protected FunctionSymbol buildTailFunction(TypeSymbol listSymbol,
                                             TypeVarSymbol typeVar) {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("tail")
        .setType(SymTypeExpressionFactory.createGenerics(
            listSymbol,
            SymTypeExpressionFactory.createTypeVariable(typeVar))
        )
        .setSpannedScope(new BasicSymbolsScope())
        .build();
  }

  protected FunctionSymbol buildAppendFunction(TypeSymbol listSymbol,
                                               TypeVarSymbol typeVar) {
    var scope = new BasicSymbolsScope();
    scope.add(SysMLv2Mill.variableSymbolBuilder()
        .setName("xs")
        .setType(SymTypeExpressionFactory.createGenerics(
            listSymbol,
            SymTypeExpressionFactory.createTypeVariable(typeVar))
        )
        .build());
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("append")
        .setType(SymTypeExpressionFactory.createGenerics(
            listSymbol,
            SymTypeExpressionFactory.createTypeVariable(typeVar))
        )
        .setSpannedScope(scope)
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

  protected FunctionSymbol buildTimesFunction(TypeSymbol streamSymbol, TypeVarSymbol typeVar) {
    var parameterList = new BasicSymbolsScope();

    VariableSymbol parameter = SysMLv2Mill.variableSymbolBuilder().setName(
        "k").setType(buildNatType()).build();
    parameterList.add(typeVar);
    parameterList.add(parameter);

    var returnType = SymTypeExpressionFactory.createGenerics(streamSymbol, SymTypeExpressionFactory.createTypeVariable(typeVar));

    return SysMLv2Mill.functionSymbolBuilder()
        .setName("times")
        .setType(returnType)
        .setSpannedScope(parameterList)
        .build();
  }

  protected FunctionSymbol buildAtTimeFunction(TypeSymbol streamSymbol,
                                               TypeVarSymbol typeVar) {
    var scope = new BasicSymbolsScope();
    scope.add(SysMLv2Mill.variableSymbolBuilder()
        .setName("t")
        .setType(buildNatType())
        .build());

    return SysMLv2Mill.functionSymbolBuilder()
        .setName("atTime")
        .setType(SymTypeExpressionFactory.createGenerics(
            streamSymbol,
            SymTypeExpressionFactory.createTypeVariable(typeVar))
        )
        .setSpannedScope(scope)
        .build();
  }

  protected FunctionSymbol buildMessagesFunction(TypeSymbol streamSymbol,
                                                 TypeVarSymbol typeVar) {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("messages")
        .setType(SymTypeExpressionFactory.createGenerics(
            streamSymbol,
            SymTypeExpressionFactory.createTypeVariable(typeVar))
        )
        .setSpannedScope(new BasicSymbolsScope())
        .build();
  }

  protected FunctionSymbol buildTakesFunction(TypeSymbol streamSymbol, TypeVarSymbol typeVar) {
    var parameterList = new BasicSymbolsScope();

    VariableSymbol parameter = SysMLv2Mill.variableSymbolBuilder()
        .setName("k")
        .setType(buildNatType())
        .build();

    parameterList.add(typeVar);
    parameterList.add(parameter);

    var returnType = SymTypeExpressionFactory.createGenerics(
        streamSymbol,
        SymTypeExpressionFactory.createTypeVariable(typeVar)
    );

    return SysMLv2Mill.functionSymbolBuilder()
        .setName("takes")
        .setType(returnType)
        .setSpannedScope(parameterList)
        .build();
  }

  protected FunctionSymbol buildInfTimesFunction(TypeSymbol streamSymbol, TypeVarSymbol typeVar) {
    var parameterList = new BasicSymbolsScope();
    parameterList.add(typeVar);

    var returnType = SymTypeExpressionFactory.createGenerics(
        streamSymbol,
        SymTypeExpressionFactory.createTypeVariable(typeVar)
    );

    return SysMLv2Mill.functionSymbolBuilder()
        .setName("infTimes")
        .setType(returnType)
        .setSpannedScope(parameterList)
        .build();
  }
}
