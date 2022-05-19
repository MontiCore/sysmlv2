package schrott.types.check;

import de.monticore.lang.sysml.sysml4verification.SysML4VerificationMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.List;
import java.util.Optional;

/**
 * TypeCheckHelper creates and adds mock types to the global scope for helping with type check
 */
public class TypeCheckHelper {

  private static final String INT = "Integer";

  private static final String DOUBLE = "Double";

  private static final String FLOAT = "Float";

  private static final String SHORT = "Short";

  private static final String LONG = "Long";

  private static final String BOOLEAN = "Boolean";

  private static final String BYTE = "Byte";

  private static final String CHAR = "Character";

  private static final String STRING = "String";

  public static final List<String> BOXED_PRIMITIVE_LIST = Lists.newArrayList(INT, DOUBLE, FLOAT, SHORT, LONG, BOOLEAN, BYTE, CHAR, STRING);

  public static void init() {
    addNatType();
    addStreamType();
    addBoxedPrimitives();
  }

  /**
   * Adds "nat" locally to global scope (if not already present)
   */
  protected static void addNatType() {
    // Since this is called during ST creation, we can only use `resolveTypeLocally` (e.g., `resolveMany` does bottom-up
    // and intra-model resolution, which is not available at the time of running this code)
    if (SysML4VerificationMill.globalScope().resolveTypeLocally("nat").isEmpty()) {
      TypeSymbol s = SysML4VerificationMill.typeSymbolBuilder()
          .setName("nat")
          .setSpannedScope(SysML4VerificationMill.scope())
          .build();
      SysML4VerificationMill.globalScope().add(s);
    }
  }

  /**
   * Adds boxed primitives locally to global scope (if not already present)
   */
  protected static void addBoxedPrimitives() {
    for (String primitive : BOXED_PRIMITIVE_LIST) {
      if (SysML4VerificationMill.globalScope().resolveTypeLocally(primitive).isEmpty()) {
        TypeSymbol s = SysML4VerificationMill.typeSymbolBuilder()
            .setName(primitive)
            .setSpannedScope(SysML4VerificationMill.scope())
            .build();
        SysML4VerificationMill.globalScope().add(s);
      }
    }
  }

  /**
   * Adds "Stream" locally to global scope (if not already present)
   */
  protected static void addStreamType() {
    // Since this is called during ST creation, we can only use `resolveTypeLocally` (e.g., `resolveMany` does bottom-up
    // and intra-model resolution, which is not available at the time of running this code)
    if (SysML4VerificationMill.globalScope().resolveTypeLocally("Stream").isEmpty()) {
      TypeVarSymbol typeVarSymbol = SysML4VerificationMill.typeVarSymbolBuilder().setName("T").build();

      TypeSymbol streamSymbol = SysML4VerificationMill.typeSymbolBuilder()
          .setName("Stream")
          .setEnclosingScope(SysML4VerificationMill.globalScope())
          .build();
      streamSymbol.setSpannedScope(SysML4VerificationMill.scope());
      streamSymbol.getSpannedScope().setName("Stream");
      streamSymbol.addTypeVarSymbol(typeVarSymbol);

      SysML4VerificationMill.globalScope().add(streamSymbol);
      SysML4VerificationMill.globalScope().addSubScope(streamSymbol.getSpannedScope());

      FunctionSymbol function = createMethod(streamSymbol, "nth");
      Optional<TypeSymbol> nat = SysML4VerificationMill.globalScope().resolveTypeLocally("nat");
      addParameter(function, "index", SymTypeExpressionFactory.createTypeExpression(nat.get()));
      function.setReturnType(SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));

      // name here should like be only 'T' but it does not resolve later unless 'Stream.T' is used
      function.getReturnType().getTypeInfo().setName("Stream.T");
      streamSymbol.getSpannedScope().add(function);
    }
  }

  protected static void addParameter(FunctionSymbol function, String paramName,
                                     SymTypeExpression paramType) {
    VariableSymbol param = SysML4VerificationMill.variableSymbolBuilder()
        .setName(paramName)
        .setEnclosingScope(function.getSpannedScope())
        .setType(paramType)
        .build();

    // setting spanned scope in builder has no effect, so we set directly in the symbol
    function.getSpannedScope().add(param);
  }

  protected static FunctionSymbol createMethod(TypeSymbol symbol, String name) {
    FunctionSymbol functionSymbol = SysML4VerificationMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(symbol.getSpannedScope())
        .build();
    functionSymbol.setSpannedScope(SysML4VerificationMill.scope());
    return functionSymbol;
  }

}
