/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2GlobalScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMillTOP.getMill;
import java.util.stream.Collectors;

public class SysMLv2Mill extends SysMLv2MillTOP {

  /**
   * Prepares the current global scope, i.e., adds symbols to it.
   * Does not initialize anything else, especially not the
   * Mill itself. Does not clear the scope beforehand.
   */
  public static void prepareGlobalScope() {
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.loadScalarValuesFromSym();
    SysMLv2Mill.addScalarFunctionsTypes();
    SysMLv2Mill.addKermlCollectionsTypes();
    SysMLv2Mill.addVectorValuesTypes();
    SysMLv2Mill.addCollectionTypes();
    SysMLv2Mill.addTsynVariables();
    SysMLv2Mill.addStatesTypes();
    SysMLv2Tool.loadStreamSymbolsFromJar();
  }

  protected static void loadScalarValuesFromSym() {
    getMill()._loadScalarValuesFromSym();
  }

  protected void _loadScalarValuesFromSym() {
    URL url = SysMLv2Tool.class.getClassLoader().getResource(
        "ScalarValues.kermlsym");

    if (url != null) {
      var globalScope = (SysMLv2GlobalScope) SysMLv2Mill.globalScope();
      boolean packageAlreadyLoaded = globalScope.getLocalSysMLPackageSymbols().stream()
          .anyMatch(symbol -> "ScalarValues".equals(symbol.getFullName()));
      if (packageAlreadyLoaded) {
        return;
      }

      var scalarValuesScope = globalScope.getSymbols2Json().load(url);
      var scalarValues = SysMLv2Mill.sysMLPackageSymbolBuilder()
          .setName("ScalarValues")
          .setFullName("ScalarValues")
          .setPackageName("")
          .setEnclosingScope(globalScope)
          .setSpannedScope(scalarValuesScope)
          .build();
      scalarValuesScope.setSpanningSymbol(scalarValues);
      globalScope.add(scalarValues);
      globalScope.addSubScope(scalarValuesScope);
    } else {
      de.se_rwth.commons.logging.Log.error("Could not find ScalarValues.kermlsym");
    }
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
    BasicSymbolsMill.initializeString();
  }

  public static void addScalarValueTypes() {
    getMill()._addScalarValueTypes();
  }

  public static void addScalarFunctionsTypes() {
    getMill()._addScalarFunctionsTypes();
  }

  protected void _addScalarValueTypes() {
    if (SysMLv2Mill.globalScope().resolveSysMLPackage("ScalarValues").isPresent()) {
      return;
    }

    var packageScope = SysMLv2Mill.scope();
    packageScope.setName("ScalarValues");
    packageScope.setEnclosingScope(SysMLv2Mill.globalScope());
    var scalarValues = SysMLv2Mill.sysMLPackageSymbolBuilder()
        .setName("ScalarValues")
        .setFullName("ScalarValues")
        .setPackageName("")
        .setEnclosingScope(SysMLv2Mill.globalScope())
        .setSpannedScope(packageScope)
        .build();
    packageScope.setSpanningSymbol(scalarValues);
    SysMLv2Mill.globalScope().add(scalarValues);

    var scalarValue = createScalarValueType(packageScope, "ScalarValue");
    var bool = createScalarValueType(packageScope, "Boolean");
    var numericalValue = createScalarValueType(packageScope, "NumericalValue");
    var number = createScalarValueType(packageScope, "Number");
    var complex = createScalarValueType(packageScope, "Complex");
    var real = createScalarValueType(packageScope, "Real");
    var rational = createScalarValueType(packageScope, "Rational");
    var integer = createScalarValueType(packageScope, "Integer");
    var natural = createScalarValueType(packageScope, "Natural");
    var positive = createScalarValueType(packageScope, "Positive");

    setScalarValueSuperTypes(bool, scalarValue);
    setScalarValueSuperTypes(numericalValue, scalarValue);
    setScalarValueSuperTypes(number, numericalValue);
    setScalarValueSuperTypes(complex, number);
    setScalarValueSuperTypes(real, complex);
    setScalarValueSuperTypes(rational, real);
    setScalarValueSuperTypes(integer, rational);
    setScalarValueSuperTypes(natural, integer);
    setScalarValueSuperTypes(positive, natural);
  }

  protected void _addScalarFunctionsTypes() {
    if (SysMLv2Mill.globalScope().resolveSysMLPackage("ScalarFunctions").isPresent()) {
      return;
    }

    var packageScope = SysMLv2Mill.scope();
    packageScope.setName("ScalarFunctions");
    packageScope.setEnclosingScope(SysMLv2Mill.globalScope());
    var scalarFunctions = SysMLv2Mill.sysMLPackageSymbolBuilder()
        .setName("ScalarFunctions")
        .setFullName("ScalarFunctions")
        .setPackageName("")
        .setEnclosingScope(SysMLv2Mill.globalScope())
        .setSpannedScope(packageScope)
        .build();
    packageScope.setSpanningSymbol(scalarFunctions);
    SysMLv2Mill.globalScope().add(scalarFunctions);


    packageScope.add(buildMinFunction());
  }

  protected OOTypeSymbol createScalarValueType(ISysMLv2Scope packageScope, String name) {
    var type = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName(name)
        .setFullName("ScalarValues." + name)
        .setPackageName("ScalarValues")
        .setEnclosingScope(packageScope)
        .setSpannedScope(scope())
        .build();
    packageScope.add(type);
    return type;
  }

  protected void setScalarValueSuperTypes(OOTypeSymbol type, OOTypeSymbol superType) {
    type.setSuperTypesList(List.of(SymTypeExpressionFactory.createTypeObject(superType)));
  }

  public static void addKermlCollectionsTypes() {
    getMill()._addCollectionsPackage();
  }

  protected void _addCollectionsPackage() {
    if (SysMLv2Mill.globalScope().resolveSysMLPackage("Collections").isPresent()) {
      return;
    }

    var packageScope = SysMLv2Mill.scope();
    packageScope.setName("Collections");
    packageScope.setEnclosingScope(SysMLv2Mill.globalScope());
    var collections = SysMLv2Mill.sysMLPackageSymbolBuilder()
        .setName("Collections")
        .setFullName("Collections")
        .setPackageName("")
        .setEnclosingScope(SysMLv2Mill.globalScope())
        .setSpannedScope(packageScope)
        .build();
    packageScope.setSpanningSymbol(collections);
    SysMLv2Mill.globalScope().add(collections);

    var collection = createCollectionType(packageScope, "Collection", "T");
    var orderedCollection = createCollectionType(packageScope, "OrderedCollection", "T");
    var uniqueCollection = createCollectionType(packageScope, "UniqueCollection", "T");
    var array = createCollectionType(packageScope, "Array", "T");
    var bag = createCollectionType(packageScope, "Bag", "T");
    var set = createCollectionType(packageScope, "Set", "T");
    var orderedSet = createCollectionType(packageScope, "OrderedSet", "T");
    var list = createCollectionType(packageScope, "List", "T");
    var keyValuePair = createCollectionType(packageScope, "KeyValuePair", "K", "V");
    var map = createCollectionType(packageScope, "Map", "K", "V");
    var orderedMap = createCollectionType(packageScope, "OrderedMap", "K", "V");

    setCollectionSuperTypes(orderedCollection, collection);
    setCollectionSuperTypes(uniqueCollection, collection);
    setCollectionSuperTypes(array, orderedCollection);
    setCollectionSuperTypes(bag, collection);
    setCollectionSuperTypes(set, uniqueCollection);
    setCollectionSuperTypes(orderedSet, orderedCollection, uniqueCollection);
    setCollectionSuperTypes(list, orderedCollection);
    setCollectionSuperTypes(orderedMap, map);

    // Map :> Collection
    var k = map.getSpannedScope().resolveTypeVarLocally("K");
    var v = map.getSpannedScope().resolveTypeVarLocally("V");
    if (k.isPresent() && v.isPresent()) {
      var kv = SymTypeExpressionFactory.createGenerics(keyValuePair,
          SymTypeExpressionFactory.createTypeVariable(k.get()),
          SymTypeExpressionFactory.createTypeVariable(v.get()));
      map.setSuperTypesList(
          List.of(SymTypeExpressionFactory.createGenerics(collection, kv)));


    }
  }

  protected OOTypeSymbol createCollectionType(ISysMLv2Scope packageScope, String name, String... typeVars) {
    var spannedScope = scope();
    for (String typeVarName : typeVars) {
      var typeVar = BasicSymbolsMill.typeVarSymbolBuilder().setName(typeVarName).build();
      spannedScope.add(typeVar);
    }

    var type = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName(name)
        .setFullName("Collections." + name)
        .setPackageName("Collections")
        .setEnclosingScope(packageScope)
        .setSpannedScope(spannedScope)
        .build();
    packageScope.add(type);
    return type;
  }

  protected void setCollectionSuperTypes(OOTypeSymbol type, OOTypeSymbol... superTypes) {
    List<SymTypeExpression> superTypeExpressions = new ArrayList<>();
    for (OOTypeSymbol superType : superTypes) {
      if (!superType.getSpannedScope().getTypeVarSymbols().isEmpty()) {
        List<SymTypeExpression> typeArgs = type.getSpannedScope().getLocalTypeVarSymbols().stream()
            .map(SymTypeExpressionFactory::createTypeVariable)
            .collect(Collectors.toList());
        superTypeExpressions.add(SymTypeExpressionFactory.createGenerics(superType, typeArgs));
      } else {
        superTypeExpressions.add(SymTypeExpressionFactory.createTypeObject(superType));
      }
    }
    type.setSuperTypesList(superTypeExpressions);
  }

  public static void addVectorValuesTypes() {
    getMill()._addVectorValuesPackage();
  }

  protected void _addVectorValuesPackage() {
    if (SysMLv2Mill.globalScope().resolveSysMLPackage("VectorValues").isPresent()) {
      return;
    }

    var packageScope = SysMLv2Mill.scope();
    packageScope.setName("VectorValues");
    packageScope.setEnclosingScope(SysMLv2Mill.globalScope());
    var vectorValues = SysMLv2Mill.sysMLPackageSymbolBuilder()
        .setName("VectorValues")
        .setFullName("VectorValues")
        .setPackageName("")
        .setEnclosingScope(SysMLv2Mill.globalScope())
        .setSpannedScope(packageScope)
        .build();
    packageScope.setSpanningSymbol(vectorValues);
    SysMLv2Mill.globalScope().add(vectorValues);

    var vectorValue = createVectorValueType(packageScope, "VectorValue");
    var numericalVectorValue = createVectorValueType(packageScope, "NumericalVectorValue");
    var cartesianVectorValue = createVectorValueType(packageScope, "CartesianVectorValue");
    var threeVectorValue = createVectorValueType(packageScope, "ThreeVectorValue");
    var cartesianThreeVectorValue = createVectorValueType(packageScope, "CartesianThreeVectorValue");

    var array = SysMLv2Mill.globalScope().resolveType("Collections.Array").get();
    var numericalValue = SysMLv2Mill.globalScope().resolveType("ScalarValues.NumericalValue").get();
    var realValue = SysMLv2Mill.globalScope().resolveType("ScalarValues.Real").get();

    // NumericalVectorValue :> VectorValue, Array<NumericalValue>
    numericalVectorValue.setSuperTypesList(List.of(
        SymTypeExpressionFactory.createTypeObject(vectorValue),
        SymTypeExpressionFactory.createGenerics(array, SymTypeExpressionFactory.createTypeObject(numericalValue))
    ));

    // CartesianVectorValue :> NumericalVectorValue
    cartesianVectorValue.setSuperTypesList(List.of(
        SymTypeExpressionFactory.createTypeObject(numericalVectorValue)
    ));

    // ThreeVectorValue :> NumericalVectorValue
    threeVectorValue.setSuperTypesList(List.of(
        SymTypeExpressionFactory.createTypeObject(numericalVectorValue)
    ));

    // CartesianThreeVectorValue :> CartesianVectorValue, ThreeVectorValue
    cartesianThreeVectorValue.setSuperTypesList(List.of(
        SymTypeExpressionFactory.createTypeObject(cartesianVectorValue),
        SymTypeExpressionFactory.createTypeObject(threeVectorValue)
    ));
  }

  protected OOTypeSymbol createVectorValueType(ISysMLv2Scope packageScope, String name) {
    var type = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName(name)
        .setFullName("VectorValues." + name)
        .setPackageName("VectorValues")
        .setEnclosingScope(packageScope)
        .setSpannedScope(scope())
        .build();
    packageScope.add(type);
    return type;
  }

  protected OOTypeSymbol buildOptionalType() {
    var typeVar = BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build();

    var spannedScope = scope();
    spannedScope.add(typeVar);

    spannedScope.add(
        SysMLv2Mill.functionSymbolBuilder()
            .setName("get")
            .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
            .setSpannedScope(scope())
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

    var spannedScope = scope();
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

  public static void addTsynVariables() {
    getMill()._addTsynVariables();
  }

  protected void _addTsynVariables() {
    if (SysMLv2Mill.globalScope().resolveVariable("Eps").isEmpty()) {
      var eps = variableSymbolBuilder()
          .setName("Eps")
          .setEnclosingScope(globalScope())
          .setFullName("Eps")
          // createTopType wird verwendet, da TopType ein übergeordneter Typ
          // aller anderen Typen ist. Dadurch schlägt TypeCheck3TransitionGuards
          // nicht bei Guards wie 'if input.val == Eps' fehl
          .setType(SymTypeExpressionFactory.createTopType())
          .setAccessModifier(AccessModifier.ALL_INCLUSION)
          .build();
      SysMLv2Mill.globalScope().add(eps);
    }
  }

  protected OOTypeSymbol buildCollectionType(String name, String... typeVars) {
    var spannedScope = scope();

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
    var parameterList = scope();

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
        .setSpannedScope(scope())
        .build();
  }

  protected FunctionSymbol buildCountFunction() {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("count")
        .setType(buildIntType())
        .setSpannedScope(scope())
        .build();
  }

  protected FunctionSymbol buildHeadFunction(TypeVarSymbol typeVar) {
    return SysMLv2Mill.functionSymbolBuilder()
        .setName("head")
        .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
        .setSpannedScope(scope())
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
        .setSpannedScope(scope())
        .build();
  }

  protected FunctionSymbol buildAppendFunction(TypeSymbol listSymbol,
                                               TypeVarSymbol typeVar) {
    var scope = scope();
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
        .setSpannedScope(scope())
        .build();
  }

  protected FunctionSymbol buildTimesFunction(TypeSymbol streamSymbol, TypeVarSymbol typeVar) {
    var parameterList = scope();

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
    var scope = scope();
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
        .setSpannedScope(scope())
        .build();
  }

  protected FunctionSymbol buildTakesFunction(TypeSymbol streamSymbol, TypeVarSymbol typeVar) {
    var parameterList = scope();

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
    var parameterList = scope();
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

  protected FunctionSymbol buildMinFunction() {
    var parameterList = scope();
    var type = SymTypeExpressionFactory.createPrimitive(
        globalScope().resolveType("long").get()
    );

    VariableSymbol x = SysMLv2Mill.variableSymbolBuilder()
        .setName("x")
        .setType(type)
        .build();
    VariableSymbol y = SysMLv2Mill.variableSymbolBuilder()
        .setName("y")
        .setType(type)
        .build();

    parameterList.add(x);
    parameterList.add(y);

    return SysMLv2Mill.functionSymbolBuilder()
        .setName("min")
        .setType(type)
        .setSpannedScope(parameterList)
        .build();
  }

  public static void addStatesTypes(){
    getMill()._addStatesTypes();
  }

  protected void _addStatesTypes() {
    if (SysMLv2Mill.globalScope().resolveSysMLPackage("States").isPresent()) {
      return;
    }
    var packageScope = SysMLv2Mill.scope();
    packageScope.setName("States");
    packageScope.setEnclosingScope(SysMLv2Mill.globalScope());

    var statesPackage = SysMLv2Mill.sysMLPackageSymbolBuilder()
        .setName("States")
        .setFullName("States")
        .setPackageName("")
        .setEnclosingScope(SysMLv2Mill.globalScope())
        .setSpannedScope(packageScope)
        .build();
    packageScope.setSpanningSymbol(statesPackage);
    SysMLv2Mill.globalScope().add(statesPackage);

    var stateActionScope = SysMLv2Mill.scope();
    stateActionScope.setName("StateAction");
    stateActionScope.setEnclosingScope(packageScope);

    var stateAction = SysMLv2Mill.stateDefSymbolBuilder()
        .setName("StateAction")
        .setFullName("States.StateAction")
        .setPackageName("States")
        .setEnclosingScope(packageScope)
        .setSpannedScope(stateActionScope)
        .build();

    packageScope.add(stateAction);
  }
}
