package types3;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types3.SysMLCommonExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLOCLExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLTypeVisitorOperatorCalculator;
import de.monticore.lang.sysmlv2.types3.SysMLWithinScopeBasicSymbolResolver;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NegationTest {

  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    LogStub.init();
  }

  @BeforeEach
  public void reset() {
    Log.getFindings().clear();
    tool.init();

    var type4Ast = new Type4Ast();
    var typeTraverser = SysMLv2Mill.traverser();

    var forBasis = new ExpressionBasisTypeVisitor();
    forBasis.setType4Ast(type4Ast);
    typeTraverser.add4ExpressionsBasis(forBasis);

    var forLiterals = new MCCommonLiteralsTypeVisitor();
    forLiterals.setType4Ast(type4Ast);
    typeTraverser.add4MCCommonLiterals(forLiterals);

    var forCommon = new SysMLCommonExpressionsTypeVisitor();
    forCommon.setType4Ast(type4Ast);
    typeTraverser.add4CommonExpressions(forCommon);
    typeTraverser.setCommonExpressionsHandler(forCommon);
    typeTraverser.add4SysMLExpressions(forCommon);
    typeTraverser.setSysMLExpressionsHandler(forCommon);

    var forOcl = new SysMLOCLExpressionsTypeVisitor();
    forOcl.setType4Ast(type4Ast);
    typeTraverser.add4OCLExpressions(forOcl);
    typeTraverser.add4SysMLExpressions(forOcl);

    var forBasicTypes = new MCBasicTypesTypeVisitor();
    forBasicTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCBasicTypes(forBasicTypes);

    var forStreams = new StreamExpressionsTypeVisitor();
    forStreams.setType4Ast(type4Ast);
    typeTraverser.add4StreamExpressions(forStreams);

    SysMLWithinScopeBasicSymbolResolver.init();
    SysMLTypeVisitorOperatorCalculator.init();
    StreamSymTypeRelations.init();

    new MapBasedTypeCheck3(typeTraverser, type4Ast).setThisAsDelegate();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { in attribute val: boolean; } part s { port f: F; constraint e { forall long t: f.val.nth(t) == !f.val.nth(t) } }"
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
    var rightType = TypeCheck3.typeOf(equalsExpr.getRight());
    var leftType = TypeCheck3.typeOf(equalsExpr.getLeft());
    assertFalse(rightType.isObscureType());
    assertThat(rightType.printFullName()).isEqualTo("UntimedStream.UntimedStream<boolean>");
    assertTrue(rightType.deepEquals(leftType));
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
    var rightType = TypeCheck3.typeOf(equalsExpr.getRight());
    var leftType = TypeCheck3.typeOf(equalsExpr.getLeft());
    assertFalse(rightType.isObscureType());
    assertThat(rightType.printFullName()).isEqualTo("boolean");
    assertTrue(rightType.deepEquals(leftType));
  }
}
