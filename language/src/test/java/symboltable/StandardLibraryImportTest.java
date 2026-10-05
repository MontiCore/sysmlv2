package symboltable;

import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlstates.symboltable.adapters.StateDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardLibraryImportTest {
  public static SysMLv2Tool tool;

  @BeforeAll
  public static void init() {
    LogStub.init();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @Test
  public void testKerMLLib() {
    var globalScope = tool.getGlobalScope();
    assertThat(globalScope.resolveSysMLPackage("ScalarValues")).isPresent();
    assertThat(globalScope.resolveSysMLPackage("Collections")).isPresent();
    assertThat(globalScope.resolveSysMLPackage("VectorValues")).isPresent();

    // Why not SysMLType?
    assertThat(globalScope.resolveType("ScalarValues.Boolean")).isPresent();
    assertThat(globalScope.resolveType("String")).isPresent();
    assertThat(globalScope.resolveType("String").get().getFullName()).isEqualTo("String");
    assertThat(globalScope.resolveType("ScalarValues.String")).isPresent();
    assertThat(globalScope.resolveType("ScalarValues.String").get().getFullName())
        .isEqualTo("ScalarValues.String");
    assertThat(globalScope.resolveType("String").get())
        .isNotSameAs(globalScope.resolveType("ScalarValues.String").get());
    assertThat(globalScope.resolveType("Collections.Bag")).isPresent();
    assertThat(globalScope.resolveType("VectorValues.CartesianVectorValue")).isPresent();

    var positive = globalScope.resolveType("ScalarValues.Positive").get();
  }

  @Test
  public void testImportedScalarStringTakesPrecedenceOverBuiltInString() throws IOException {
    var model = "private import ScalarValues::*; attribute a: String;";
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    var type = ((ASTAttributeUsage) ast.getSysMLElement(1))
        .getSpecialization(0).getSuperTypes(0);
    var typeScope = (ISysMLv2Scope) type.getEnclosingScope();

    assertThat(typeScope.resolveTypeMany(type.printType()))
        .extracting(symbol -> symbol.getFullName())
        .containsExactly("ScalarValues.String");

    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var resolvedType = typeScope.resolveType(type.printType());

    assertThat(resolvedType).isPresent();
    assertThat(resolvedType.get().getFullName()).isEqualTo("ScalarValues.String");
  }

  @Test()
  public void testFQNResolving() throws IOException {


    var model = "attribute a: ScalarValues::Complex;";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var type = ((ASTAttributeUsage) ast.getSysMLElement(0)).getSpecialization(0).getSuperTypes(0);

    type.printType();

    assertThat(type.printType()).isEqualTo("ScalarValues.Complex");
    assertThat(((ISysMLv2Scope)type.getEnclosingScope()).resolveType(type.printType())).isPresent();
  }

  @Test()
  public void testImportResolving() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model = "private import Collections::Bag; attribute a: Bag;";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var type = ((ASTAttributeUsage) ast.getSysMLElement(1)).getSpecialization(0).getSuperTypes(0);

    assertThat(type.printType()).isEqualTo("Bag");
    var resolved = ((ISysMLv2Scope) type.getEnclosingScope()).resolveType("Bag");
    assertThat(resolved).isPresent();
    assertThat(resolved.get()).isInstanceOf(TypeSymbol.class);
    assertThat(resolved.get().getFullName()).isEqualTo("Collections.Bag");
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "private import Collections::List; attribute a: List;",
      "private import Collections::*; attribute a: List;",
      "attribute a: Collections::List;"
  })
  public void testCollectionsListResolving(String model) throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var attribute = (ASTAttributeUsage) ast.getSysMLElement(ast.sizeSysMLElements() - 1);
    var type = attribute.getSpecialization(0).getSuperTypes(0);
    var resolved = ((ISysMLv2Scope) type.getEnclosingScope()).resolveType(type.printType());

    assertThat(resolved).isPresent();
    assertThat(resolved.get()).isInstanceOf(TypeSymbol.class);
    assertThat(resolved.get().getFullName()).isEqualTo("Collections.List");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testCollectionsListGenericTypeFromSymFile() throws IOException {
    LogStub.init();
    var model = "attribute l: Collections::List<ScalarValues::Boolean>;";
    SysMLv2Mill.init();
    // We clear the globalScope to load the KerMLSym
    SysMLv2Mill.globalScope().clear();

    // No call to tool.init(): since it registers the hardcoded Collections.List<T>.
    SysMLv2Mill.loadScalarValuesFromSym();
    SysMLv2Mill.loadCollectionValuesFromSym();
    assertThat(Log.getFindings()).isEmpty();

    var list = SysMLv2Mill.globalScope().resolveType("Collections.List");
    assertThat(list).isPresent();
    assertThat(list.get()).isExactlyInstanceOf(TypeSymbol.class);
    assertThat(list.get().getFullName()).isEqualTo("Collections.List");
    assertThat(list.get().getTypeParameterList())
        .as("Current limitation: Collections.kermlsym has no element type parameter")
        .isEmpty();

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var symbolFileTool = new SysMLv2Tool();
    symbolFileTool.createSymbolTable(ast);
    symbolFileTool.completeSymbolTable(ast);
    symbolFileTool.finalizeSymbolTable(ast);

    var attribute = (ASTAttributeUsage) ast.getSysMLElement(0);
    var mcType = attribute.getSpecialization(0).getSuperTypes(0);
    assertThat(mcType).isInstanceOf(ASTMCBasicGenericType.class);
    var genericType = (ASTMCBasicGenericType) mcType;
    assertThat(genericType.printWithoutTypeArguments()).isEqualTo("Collections.List");
    assertThat(genericType.getMCTypeArgumentList()).hasSize(1);
    var scope = (ISysMLv2Scope) mcType.getEnclosingScope();
    assertThat(scope.resolveType(genericType.printWithoutTypeArguments())).contains(list.get());
    assertThat(scope.resolveType("ScalarValues.Boolean")).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testStatesImport() throws IOException {
    var model = " private import States::*;"+
        "state def MyState : StateAction;";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var resolvedStateAction = ast.getEnclosingScope().resolveType("StateAction");

    assertThat(resolvedStateAction).isPresent();
    assertThat(resolvedStateAction.get()).isInstanceOf(StateDef2TypeSymbolAdapter.class);
  }
}
