package typecheck;

import de.monticore.lang.sysmlactions._ast.ASTCalcDef;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SysMLFunctionOperationExpressionTest {
  @Test
  void functionOperations() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();
    var ast = SysMLv2Mill.parser().parse_String(
        "calc def id { in value: int; return : int; }"
      + "calc def check { in value: int; in flag: boolean; return : boolean; }"
      + "package Functions { calc def id { in value: int; return : int; } }"
      + "package Predicates { calc def positive { in value: int; return : ScalarValues::Boolean; } }"
      + "attribute items: int[*]; attribute number: int;"
      + "calc def shadow { in values: int[*]; return : ScalarValues::Boolean;"
      + "  values->ControlFunctions::forAll { in values: int; values != 0 } }"
      + "constraint { 1->id } constraint { 1->id() }"
      + "constraint { id(1->id()->id()) }"
      + "constraint { 1->check(true) }"
      + "constraint { 1->Functions::id() } constraint { 1->Functions.id() }"
      + "constraint { items->ControlFunctions::forAll Predicates::positive }"
      + "constraint { items->ControlFunctions::forAll(Predicates::positive) }"
      + "constraint { true->check(true) } constraint { 1->check(2) }"
      + "constraint { 1->check } constraint { 1->check(true, false) }"
      + "constraint { 1->missing() }"
      + "constraint { 1->number() }"
      + "constraint { items->ControlFunctions::forAll missing }"
      + "constraint { items->ControlFunctions::forAll check }"
      + "constraint { items->ControlFunctions::forAll { in value: int[*]; true } }"
      + "constraint { items->ControlFunctions::forAll { in value: boolean; value } }"
      + "constraint { items->ControlFunctions::forAll { in value: int; value } }"
      + "constraint { (items->ControlFunctions::forAll { in value: int; value != 0 }) & (value != 0) }"
      + "constraint { items->ControlFunctions::forAll { in value; value != 0 } }").orElseThrow();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    assertThat(typeOf(ast, 7).printFullName()).isEqualTo("int"); // implicit call
    assertThat(typeOf(ast, 8).printFullName()).isEqualTo("int"); // explicit call
    assertThat(typeOf(ast, 9).printFullName()).isEqualTo("int"); // nested chain
    assertThat(typeOf(ast, 10).printFullName()).isEqualTo("boolean"); // extra argument
    assertThat(typeOf(ast, 11).printFullName()).isEqualTo("int"); // qualified function
    assertThat(typeOf(ast, 12).printFullName()).isEqualTo("int"); // dotted function
    assertThat(typeOf(ast, 13).printFullName()).isEqualTo("ScalarValues.Boolean"); // function reference
    assertThat(typeOf(ast, 14).printFullName()).isEqualTo("ScalarValues.Boolean"); // parenthesized reference
    assertThat(TypeCheck3.typeOf(((ASTCalcDef) ast.getSysMLElement(6)).getExpression()).printFullName())
      .isEqualTo("ScalarValues.Boolean"); // body parameter shadows outer parameter
    assertThat(typeOf(ast, 15).isObscureType()).isTrue(); // wrong receiver
    assertThat(typeOf(ast, 16).isObscureType()).isTrue(); // wrong argument
    assertThat(typeOf(ast, 17).isObscureType()).isTrue(); // missing argument
    assertThat(typeOf(ast, 18).isObscureType()).isTrue(); // too many arguments
    assertThat(typeOf(ast, 19).isObscureType()).isTrue(); // missing function
    assertThat(typeOf(ast, 20).isObscureType()).isTrue(); // non-function
    assertThat(typeOf(ast, 21).isObscureType()).isTrue(); // missing function reference
    assertThat(typeOf(ast, 22).isObscureType()).isTrue(); // incompatible function reference
    assertThat(typeOf(ast, 23).isObscureType()).isTrue(); // body parameter multiplicity
    assertThat(typeOf(ast, 24).isObscureType()).isTrue(); // body parameter type
    assertThat(typeOf(ast, 25).isObscureType()).isTrue(); // body result type
    assertThat(typeOf(ast, 26).isObscureType()).isTrue(); // body parameter is not exported
    assertThat(typeOf(ast, 27).isObscureType()).isTrue(); // missing body parameter type
  }

  private SymTypeExpression typeOf(ASTSysMLModel ast, int index) {
    return TypeCheck3.typeOf(((ASTConstraintUsage) ast.getSysMLElement(index)).getExpression());
  }
}
