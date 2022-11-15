package de.monticore.lang.sysmlv2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;

public class SysMLv2Mill extends SysMLv2MillTOP {

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

    return OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Stream")
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
    var spannedScopeOfLength = new BasicSymbolsScope();
    return SysMLv2Mill.functionSymbolBuilder().setName("length").setType(buildIntType()).setSpannedScope(spannedScopeOfLength).build();
  }
}
