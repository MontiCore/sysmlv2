package typecheck;

import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLExpressionsDeriver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeCheck4CardinalExpressionsTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init(){
    Log.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset(){
    SysMLv2Mill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    SysMLv2Mill.addStreamType();
    Log.getFindings().clear();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean[2]; } part s { port f: F; constraint e { f.a[1] } }",
      "port def F { attribute a: boolean; } part s { port f: F[1]; constraint e { f[1].a } }"
  }) public void test4ValidExpr1(String model) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysMLModel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysMLModel);
    st.completeSymbolTable(astSysMLModel);
    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var deriver = new SysMLExpressionsDeriver();
    var type = deriver.deriveType(expr);
    assertTrue(type.isPresentResult());
    assertThat(type.getResult().printFullName()).isEqualTo("Stream<boolean>");
  }

  @Test
  @Disabled
  public void test4InvalidExpr() throws IOException {
    var model = "port def F { attribute a: boolean; } part s { port f: F[2]; constraint e { f.a } }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysMLModel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysMLModel);
    st.completeSymbolTable(astSysMLModel);
    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var deriver = new SysMLExpressionsDeriver();
    var type = deriver.deriveType(expr);
    assertTrue(type.getResult().isObscureType());
    assertTrue(!Log.getFindings().isEmpty());
  }
}


