package typecheck;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.types3.TypeCheck3;
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
      "attribute target : boolean; attribute source : ScalarValues::Boolean;",
      "attribute target : int; attribute source : ScalarValues::Rational;",
      "attribute target : float; attribute source : ScalarValues::Real;",
      "attribute target : ScalarValues::Natural; attribute source : ScalarValues::Real;",
      "attribute target : ScalarValues::Integer; attribute source : ScalarValues::Rational;",
      "attribute target : ScalarValues::Integer; attribute source : int;",
      "attribute target : ScalarValues::Integer; attribute source : ScalarValues::Natural;",
      "attribute target : double; attribute source : ScalarValues::Real;",
      "attribute target : int; attribute source : ScalarValues::Positive;",
      "attribute target : ScalarValues::Positive; attribute source : ScalarValues::Natural;",
      "attribute target : ScalarValues::String; attribute source : String;",
      "attribute target : ScalarValues::Natural; attribute source : nat;"
  })
  public void test4CompatibleTypes(String targetAndSource) throws IOException {
    var type = typeOfConstraintExpression(targetAndSource);

    assertTrue(type.isPrimitive());
    assertThat(type.printFullName()).isEqualTo("boolean");
    assertTrue(Log.getFindings().isEmpty());
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = {
      "attribute target : Set<ScalarValues::String>; attribute source : Collections::Set<ScalarValues::String>;",
      "attribute target : List<ScalarValues::String>; attribute source : Collections::List<ScalarValues::String>;",
      "attribute target : Map<ScalarValues::String, ScalarValues::Integer>; attribute source : Collections::Map<ScalarValues::String, ScalarValues::Integer>;",
  })
  public void test4CompatibleGenericTypes(String targetAndSource) throws IOException {
    var type = typeOfConstraintExpression(targetAndSource);

    assertTrue(type.isPrimitive());
    assertThat(type.printFullName()).isEqualTo("boolean");
    assertTrue(Log.getFindings().isEmpty());
  }

  // int und ScalarValues::Integer sollte erlaubt sein, aber das muss noch implementiert werden
  @ParameterizedTest
  @ValueSource(strings = {
      "attribute target : int; attribute source : ScalarValues::String;"
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

  @Test
  public void test4ConstructedStreamCompatibility() throws IOException {
    var model =
      "port def Booleans { in attribute val: ScalarValues::Boolean; }" +
        "part def myPart { " +
          "port source : Booleans;" +
          "assert constraint e {" +
            "source.val == <true>" +
          "}" +
        "}";

    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();

    var astSysMLModel = ast.get();
    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var astPartDef = astSysMLModel.getSysMLElementList().get(1);
    var constraintUsage = ((ASTPartDef) astPartDef).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var type = TypeCheck3.typeOf(expr);

    assertTrue(type.isPrimitive());
    assertThat(type.printFullName()).isEqualTo("boolean");
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void test4InferenceVariables() throws IOException {
    var model =
    "part def test {"
    + "    assert constraint test {"
    + "      Stream.projSnd(<(1, true)>)"
    + "        == Stream.projSnd(<(1, true)>)"
    + "}}";

    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();

    var astSysMLModel = ast.get();
    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var astPartDef = astSysMLModel.getSysMLElementList().get(0);
    var constraintUsage = ((ASTPartDef) astPartDef).getSysMLElement(0);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var left = ((ASTEqualsExpression) expr).getLeft();
    var right = ((ASTEqualsExpression) expr).getRight();

    var typeLeft = TypeCheck3.typeOf(left);
    var typeRight = TypeCheck3.typeOf(right);

//    System.out.println("Left Side is: " + typeLeft.printFullName());
//    System.out.println("Right Side is: " + typeRight.printFullName());
    var type = TypeCheck3.typeOf(expr);
//    System.out.println("Equals evaluated to: " + type.printFullName());

    assertTrue(type.isPrimitive());
    assertThat(type.printFullName()).isEqualTo("boolean");
    assertTrue(Log.getFindings().isEmpty());
  }
}
