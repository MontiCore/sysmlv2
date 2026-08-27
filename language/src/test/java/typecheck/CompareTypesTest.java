package typecheck;

import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types3.SysMLCommonExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLMCBasicTypesTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLOCLExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSetExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
import de.monticore.lang.sysmlv2.types3.SysMLTypeVisitorOperatorCalculator;
import de.monticore.lang.sysmlv2.types3.SysMLWithinScopeBasicSymbolResolver;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompareTypesTest {

  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void setup() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void clear() {
    Log.clearFindings();
    tool.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "attribute target : boolean; attribute source : ScalarValues::Boolean;"
  })
  public void test4CompatibleTypes(String targetAndSource) throws IOException {
    var type = typeOfConstraintExpression(targetAndSource);

    assertTrue(type.isPrimitive());
    assertThat(type.printFullName()).isEqualTo("boolean");
    assertTrue(Log.getFindings().isEmpty());
  }

  // int und ScalarValues::Integer sollte erlaubt sein, aber das muss noch implementiert werden
  @ParameterizedTest
  @ValueSource(strings = {
      "attribute target : int; attribute source : ScalarValues::Integer;"
  })
  public void test4IncompatibleTypes(String targetAndSource) throws IOException {
    var type = typeOfConstraintExpression(targetAndSource);

    assertTrue(type.isObscureType());
    assertFalse(Log.getFindings().isEmpty());
  }

  protected de.monticore.types.check.SymTypeExpression typeOfConstraintExpression(
      String targetAndSource) throws IOException {
    var model =
        "private import ScalarValues::Boolean;" +
        "part def myPart { " +
          targetAndSource +
          "assert constraint e {" +
            "target == source" +
          "}" +
        "}"
        ;

    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();

    var astSysMLModel = ast.get();
    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartDef = sysmlelements.get(1);
    var constraintUsage = ((ASTPartDef) astPartDef).getSysMLElement(2);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();

    return TypeCheck3.typeOf(expr);
  }
}
