package symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SymTabTest {
  private SysMLv2Tool tool;

  @Test
  public void testStream() throws IOException {
    LogStub.init();
    Log.getFindings().clear();

    tool = new SysMLv2Tool();
    tool.init();
    Log.ensureInitialization();

    var model = "constraint c { <true>.hasInfiniteLen() == false }";
    var optAst = SysMLv2Mill.parser().parse_String(model);

    assertThat(optAst).isPresent();
    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var element = ast.getSysMLElement(0);
    ASTConstraintUsage constraint = (ASTConstraintUsage) element;
    ASTExpression expr = constraint.getExpression();

    var streamSymOpt = SysMLv2Mill.globalScope().resolveType("Stream");
    Assertions.assertTrue(streamSymOpt.isPresent(), "Stream symbol should be resolvable!");

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    checker.checkAll(ast);
    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

}
