package typecheck;

import de.monticore.lang.sysmlactions._ast.ASTCalcDef;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ForAllTest {
  private SysMLv2Tool tool;

  @BeforeEach
  public void setUp() {
    LogStub.init();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @Test
  public void testForAllWithArray() throws IOException {
    var type = typeOfConstraint(
        "attribute values: int[*];"
        + "calc def test { in x: int; return : ScalarValues::Boolean; }"
        + "constraint { ControlFunctions::forAll(values, test) }");
    assertThat(type.printFullName()).isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testForAllWithCollectionArray() throws IOException {
    var type = typeOfConstraint(
        "attribute values: Collections::Array<int>;"
        + "calc def test { in x: int; return : ScalarValues::Boolean; }"
        + "constraint { ControlFunctions::forAll(values, test) }");
    assertThat(type.printFullName()).isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testArrayCompatibility() {
    var integer = SymTypeExpressionFactory.createPrimitive("int");
    var array = SymTypeExpressionFactory.createTypeArray(integer, 1);
    var collectionArray = SymTypeExpressionFactory.createGenerics(
      SysMLv2Mill.globalScope()
        .resolveType("Collections.Array").orElseThrow(),
        integer
    );
    var orderedCollection = SymTypeExpressionFactory.createGenerics(
      SysMLv2Mill.globalScope()
        .resolveType("Collections.OrderedCollection").orElseThrow(),
      integer
    );
    assertThat(SymTypeRelations.isCompatible(orderedCollection, array)).isTrue();
    assertThat(SymTypeRelations.isCompatible(orderedCollection, collectionArray)).isTrue();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testRejectsWrongPredicateArgument() throws IOException {
    var type = typeOfConstraint(
        "attribute values: int[*];"
        + "calc def test { in x: ScalarValues::Boolean; return : ScalarValues::Boolean; }"
        + "constraint { ControlFunctions::forAll(values, test) }");
    assertThat(type.isObscureType()).isTrue();
    assertThat(Log.getErrorCount()).isPositive();
  }

  @Test
  public void testRejectsNonBooleanPredicate() throws IOException {
    var type = typeOfConstraint(
        "attribute values: Collections::Array<int>;"
        + "calc def test { in x: int; return : int; }"
        + "constraint { ControlFunctions::forAll(values, test) }");
    assertThat(type.isObscureType()).isTrue();
    assertThat(Log.getErrorCount()).isPositive();
  }

  @Test
  @Disabled("Typprüfung von -> noch nicht implementiert")
  public void testTypeOfForAllExpression() throws IOException {
    var ast = completeModel(
        "calc def usesForAll {"
        + "  in inputArray: int[*]; return : ScalarValues::Boolean;"
        + "  inputArray->ControlFunctions::forAll { in input: int; input != 0 }"
        + "}");
    var calculation = (ASTCalcDef) ast.getSysMLElement(0);
    assertThat(TypeCheck3.typeOf(calculation.getExpression()).printFullName())
        .isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  private SymTypeExpression typeOfConstraint(String model) throws IOException {
    var ast = completeModel(model);
    var constraint = (ASTConstraintUsage) ast.getSysMLElement(ast.sizeSysMLElements() - 1);
    return TypeCheck3.typeOf(constraint.getExpression());
  }

  private ASTSysMLModel completeModel(String model) throws IOException {
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    return ast;
  }
}
