package typecheck;

import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLExpressionsDeriver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * <p>This test is about TypeCheck.</p>
 *
 * <p>In this test, the expression is calculated by creating SysMLExpressionsDeriver with a boolean parameter.</p>
 *
 * <p>When ASTFieldAccessExpression and ASTOCLArrayQualification are in StateUsage, this parameter
 *  is set to false and we test whether they are not calculated as Stream.</p>
 *
 * <p>For comparison, we set this parameter is set to true (This is the same as creating
 * SysMLExpressionsDeriver without parameter) for ASTFieldAccessExpression in ConstraintUsage,
 * we test whether ASTFieldAccessExpression will still be calculated as Stream.</p>
 */
public class FieldAccessExpressionInStateUsageTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    SysMLv2Mill.addStreamType();
    Log.getFindings().clear();
  }

  static Stream<Arguments> createInputs() {
    return Stream.of(
        Arguments.of(
            "port def F { attribute a: boolean; }" +
                "part def X { port f: F; state s { transition first S if f.a then S; } }",
            false),
        Arguments.of(
            "port def F { attribute a: boolean[3]; }" +
                "part def X { port f: F; state s { transition first S if f.a[1] then S; } }",
            false),
        Arguments.of(
            "port def F { attribute a: boolean; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1].a then S; } }",
            false),
        Arguments.of(
            "port def F { attribute a: boolean[3]; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1].a[1] then S; } }",
            false),
        Arguments.of(
            "port def F { attribute a: boolean; } part def X { port f: F; constraint e { f.a } }",
            true)
    );
  }

  @ParameterizedTest
  @MethodSource({ "createInputs" })
  public void test(String model, boolean isStream) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var deriver = new SysMLExpressionsDeriver(isStream);
    var astSysmlmodel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysmlmodel);
    st.completeSymbolTable(astSysmlmodel);
    var astPartdef = astSysmlmodel.getSysMLElementList().get(1);
    var astSysmlelement = ((ASTPartDef) astPartdef).getSysMLElement(1);
    if (astSysmlelement instanceof ASTStateUsage) {
      var astTransition = ((ASTStateUsage) astSysmlelement).getSysMLElement(0);
      var expr = ((ASTSysMLTransition) astTransition).getGuard();
      var type = deriver.deriveType(expr);
      assertThat(type.getResult().printFullName()).isEqualTo("boolean");
    } else if (astSysmlelement instanceof ASTConstraintUsage) {
      var expr = ((ASTConstraintUsage) astSysmlelement).getExpression();
      var type = deriver.deriveType(expr);
      assertThat(type.getResult().printFullName()).isEqualTo("Stream<boolean>");
    } else {
      // TODO The test is too complex
      Assertions.fail("ASTSysMLElement should here be ASTStateUsage or ASTConstraintUsage");
    }
  }

  @Test
  public void testProblem() throws IOException {
    var ast = parser.parse_String("attribute a: boolean;");
    assertThat(ast).isPresent();
    var astSysmlmodel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysmlmodel);
    st.completeSymbolTable(astSysmlmodel);
    ASTAttributeUsage attr = (ASTAttributeUsage) astSysmlmodel.getSysMLElementList().get(0);
    assertThat(attr.getEnclosingScope().resolveVariable("a").get().getType().printFullName()).isEqualTo("boolean");
  }

  @Test
  public void testProblem2() throws IOException {
    var ast = parser.parse_String("port def P { attribute a: boolean; }");
    assertThat(ast).isPresent();
    var astSysmlmodel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysmlmodel);
    st.completeSymbolTable(astSysmlmodel);
    var attr = (ASTPortDef) astSysmlmodel.getSysMLElementList().get(0);
    assertThat(attr.getSpannedScope().resolveVariable("a").get().getType().printFullName()).isEqualTo("boolean");
  }
}
