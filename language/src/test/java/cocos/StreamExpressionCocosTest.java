/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamExpressionCocosTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/constraints";

  private SysMLv2Tool tool;

  @BeforeAll public static void init() {
    LogStub.init();
    OCLSymTypeRelations.init();
    SysMLv2Mill.init();
  }

  @BeforeEach public void reset() {
    Log.getFindings().clear();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @ParameterizedTest()
  @ValueSource(strings = {
      "streamRepeat.sysml",
  })
  public void teststreamRepeat(String modelName) throws IOException {
    var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    checker.checkAll(ast);
    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest()
  @ValueSource(strings = {
      "streamExpression.sysml",
  })
  public void testStreamExpression(String modelName) throws IOException {
    var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    checker.checkAll(ast);
    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }


  @Test
  public void test() throws IOException {
    var model = "port def Dummy { constraint c { <true>} }";
    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());
    assertThat(Log.getFindings()).isEmpty();

    var constraint = (ASTConstraintUsage)((ASTPortDef)ast.get().getSysMLElement(0)).getSysMLElement(0);
    var expr = constraint.getExpression();

    var deriver = new SysMLDeriver(true);
    var type = deriver.deriveType(expr);

    assertThat(type.isPresentResult());
    System.out.println("Type of true: " + type.getResult().printFullName());
    assertThat(type.getResult().printFullName()).isEqualTo("Stream<boolean>");
  }

}
