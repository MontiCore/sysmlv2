package typecheck;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NegationTest {

  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void init(){
    LogStub.init();
  }

  @BeforeEach
  public void reset() {
    Log.getFindings().clear();
    tool.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { in attribute val: boolean; } part s { port f: F; constraint e { forall nat t: f.val.atTime(t) == !f.val.atTime(t) } }"
  })
  public void testNotBooleanStream(String model) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var forAllExpr = (ASTForallExpression)(((ASTConstraintUsage) constraintUsage).getExpression());
    var equalsExpr = (ASTEqualsExpression) forAllExpr.getExpression();
    var deriver = new SysMLDeriver();
    var rightType = deriver.deriveType(equalsExpr.getRight());
    var leftType = deriver.deriveType(equalsExpr.getLeft());
    assertTrue(rightType.isPresentResult());
    assertThat(rightType.getResult().printFullName()).isEqualTo("Stream<boolean>");
    assertThat(rightType.getResult().equals(leftType.getResult()));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "part s { constraint e { forall nat t: true == !false } }"
  })
  public void testNotBoolean(String model) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(0);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(0);
    var forAllExpr = (ASTForallExpression)(((ASTConstraintUsage) constraintUsage).getExpression());
    var equalsExpr = (ASTEqualsExpression) forAllExpr.getExpression();
    var deriver = new SysMLDeriver();
    var rightType = deriver.deriveType(equalsExpr.getRight());
    var leftType = deriver.deriveType(equalsExpr.getLeft());
    assertTrue(rightType.isPresentResult());
    assertThat(rightType.getResult().printFullName()).isEqualTo("boolean");
    assertThat(rightType.getResult().equals(leftType.getResult()));
  }
}
