package symboltable;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the completion of the Symbol Table, particularly related to types and type check.
 */
public class SymbolTableCompletionTest {

  private SysMLv2Parser parser;

  private SysMLv2Tool tool;

  @BeforeAll
  public static void setup() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void init() {
    tool = new SysMLv2Tool();
    tool.init();
    parser = SysMLv2Mill.parser();
  }

  /**
   * Datatype Stream defines a method "values" with no parameters and return type {@literal Set<E>}
   */
  @Test
  public void testStreamValues() throws IOException {
    var model = "port def P { attribute a: int; } part def S { port p: P; constraint c { p.a.values() } }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());
    assertThat(Log.getFindings()).isEmpty();

    var expr = ((ASTConstraintUsage)((ASTPartDef)ast.get().getSysMLElement(1)).getSysMLElement(1)).getExpression();
    var deriver = new SysMLDeriver(true);
    var type = deriver.deriveType(expr);
    assertThat(type.isPresentResult());
    assertThat(type.getResult().printFullName()).isEqualTo("Set<int>");
  }

  /**
   * The OCL is a little particular about its quantifiers. Lets check that the ST-Completer gets this correctly!
   */
  @Test
  public void testStreamValuesInQuantified() throws IOException {
    var model = "port def P { attribute a: int; }\n" +
        "part def S { port p: P; constraint c { forall v in p.a.values(): v > 1 } }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();

    // The OCL-ST-Completer will atempt to determine the type of "p.a.values()"
    // and requires a Collection of some description!
    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());
    assertThat(Log.getFindings()).isEmpty();

    var expr = ((ASTConstraintUsage)((ASTPartDef)ast.get().getSysMLElement(1)).getSysMLElement(1)).getExpression();
    var in = ((ASTForallExpression)expr).getInDeclaration(0).getExpression();
    assertThat(in).isInstanceOf(ASTCallExpression.class);
    // The definition should be a function without parameters and return type Set<int>
    assertThat(((ASTCallExpression)in).getDefiningSymbol()).isPresent();
    assertThat(((ASTCallExpression)in).getDefiningSymbol().get().getFullName()).isEqualTo("Stream.values");
    assertThat(((ASTCallExpression)in).getDefiningSymbol().get()).isInstanceOf(FunctionSymbol.class);
    assertThat(((FunctionSymbol)((ASTCallExpression)in).getDefiningSymbol().get()).getFunctionType().printFullName())
        .isEqualTo("() -> Set<int>");
  }


  /**
   * Custom types, i.e., types defined using attribute definitions, do not work instantly?
   */
  @Test
  public void testAttributeDefs() throws IOException {
    var model = "attribute def A { attribute a: int; } port def P { attribute a: A; }\n" +
        "part def S { port p: P; constraint c { forall v in p.a.values(): v.a > 1 } }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();

    // The OCL-ST-Completer will atempt to determine the type of "p.a.values()"
    // and requires a Collection of some description!
    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());
    assertThat(Log.getFindings()).isEmpty();

    var expr = ((ASTConstraintUsage)((ASTPartDef)ast.get().getSysMLElement(2)).getSysMLElement(1)).getExpression();
    var in = ((ASTForallExpression)expr).getInDeclaration(0).getExpression();
    assertThat(in).isInstanceOf(ASTCallExpression.class);

    // The definition should be a function without parameters and return type Set<int>
    assertThat(((ASTCallExpression)in).getDefiningSymbol()).isPresent();
    assertThat(((ASTCallExpression)in).getDefiningSymbol().get().getFullName()).isEqualTo("Stream.values");
    assertThat(((ASTCallExpression)in).getDefiningSymbol().get()).isInstanceOf(FunctionSymbol.class);
    assertThat(((FunctionSymbol)((ASTCallExpression)in).getDefiningSymbol().get()).getFunctionType().printFullName())
        .isEqualTo("() -> Set<A>");
  }

  /**
   * The exact same thing, but with multiple artifacts. And these artifacts are not ordered!
   */
  @Test
  public void testAttributeDefsArtifacts() throws IOException {
    var models = List.of(
        "part def S { port p: ~P; attribute x: nat; constraint c { forall v in p.a.values(): v.a > x } }",
        "port def P { attribute a: A; }",
        "attribute def A { attribute a: boolean; }"
    );
    List<ASTSysMLModel> asts = new ArrayList<>();
    for(var model: models) {
      Optional<ASTSysMLModel> ast = parser.parse_String(model);
      assertThat(ast).isPresent();
      asts.add(ast.get());
    }

    // The OCL-ST-Completer will atempt to determine the type of "p.a.values()"
    // and requires a Collection of some description!
    asts.forEach(ast -> tool.createSymbolTable(ast));
    asts.forEach(ast -> tool.completeSymbolTable(ast));
    asts.forEach(ast -> tool.finalizeSymbolTable(ast));
    assertThat(Log.getFindings()).isEmpty();

    var expr = ((ASTConstraintUsage)((ASTPartDef)asts.get(0).getSysMLElement(0)).getSysMLElement(2)).getExpression();
    var in = ((ASTForallExpression)expr).getInDeclaration(0).getExpression();
    assertThat(in).isInstanceOf(ASTCallExpression.class);

    // The definition should be a function without parameters and return type Set<int>
    assertThat(((ASTCallExpression)in).getDefiningSymbol()).isPresent();
    assertThat(((ASTCallExpression)in).getDefiningSymbol().get().getFullName()).isEqualTo("Stream.values");
    assertThat(((ASTCallExpression)in).getDefiningSymbol().get()).isInstanceOf(FunctionSymbol.class);
    assertThat(((FunctionSymbol)((ASTCallExpression)in).getDefiningSymbol().get()).getFunctionType().printFullName())
        .isEqualTo("() -> Set<A>");
  }

  @Test
  public void testEnums() throws IOException {
    var models = List.of(
        "enum def E { enum X; enum Y; }",
        "port def P { attribute a: E; }",
        "part def S { port p:P; constraint c { p.a.snth(0) == E.X } }"
    );

    List<ASTSysMLModel> asts = new ArrayList<>();
    for(var model: models) {
      Optional<ASTSysMLModel> ast = parser.parse_String(model);
      assertThat(ast).isPresent();
      asts.add(ast.get());
    }

    asts.forEach(ast -> tool.createSymbolTable(ast));
    asts.forEach(ast -> tool.completeSymbolTable(ast));
    asts.forEach(ast -> tool.finalizeSymbolTable(ast));
    assertThat(Log.getFindings()).isEmpty();

    asts.forEach(ast -> tool.runDefaultCoCos(ast));
    asts.forEach(ast -> tool.runAdditionalCoCos(ast));
  }

}
