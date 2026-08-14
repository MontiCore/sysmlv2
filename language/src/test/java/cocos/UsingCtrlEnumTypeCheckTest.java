package cocos;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class UsingCtrlEnumTypeCheckTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    SysMLv2Mill.reset();
  }

  @Test
  public void tryTypeCheckingCtrlEnumName() throws IOException {
    SysMLv2Tool tool = new SysMLv2Tool();
    tool.init();

    var model = "enum def E { enum e; } constraint { E::e }";
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    // extrahiere das "E" aus "enum def ...; constraint { E::e }" im Model
    var expr = ((ASTConstraintUsage) ast.getSysMLElement(1)).getExpression();
    var E = ((ASTFieldAccessExpression)expr).getExpression();

    var type = TypeCheck3.typeOf(E);

    assertThat(type.isObscureType()).isFalse();
    assertThat(type.hasTypeInfo()).isTrue();
    assertThat(type.getTypeInfo().getFullName()).isEqualTo("E");

    assertThat(Log.getFindings())
        .filteredOn(Finding::isError)
        .isEmpty();
  }
}
