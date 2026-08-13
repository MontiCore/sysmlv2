package cocos;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
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

    ASTSysMLModel ast = tool.parse(
        Path.of("src/test/resources/dluf/models/UsingCtrlEnum.sysml").toString()
    );

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    ASTNameExpression ctrlEnum = findCtrlEnumNameExpression(ast);

    var type = TypeCheck3.typeOf(ctrlEnum);

    assertThat(type.isObscureType()).isFalse();
    assertThat(type.hasTypeInfo()).isTrue();
    assertThat(type.getTypeInfo().getFullName()).isEqualTo("CtrlEnum");

    assertThat(Log.getFindings())
        .filteredOn(Finding::isError)
        .isEmpty();
  }

  private ASTNameExpression findCtrlEnumNameExpression(ASTSysMLModel ast) {
    AtomicReference<ASTNameExpression> result = new AtomicReference<>();

    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4CommonExpressions(new CommonExpressionsVisitor2() {
      @Override
      public void visit(ASTFieldAccessExpression node) {
        if ("NAK".equals(node.getName())
            && node.getExpression() instanceof ASTNameExpression
            && "CtrlEnum".equals(((ASTNameExpression) node.getExpression()).getName())) {
          result.set((ASTNameExpression) node.getExpression());
        }
      }
    });

    ast.accept(traverser);

    assertThat(result.get()).isNotNull();

    return result.get();
  }
}
