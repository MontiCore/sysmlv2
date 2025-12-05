package types3;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBooleanTC3;
import de.monticore.lang.sysmlv2.types3.SysMLCommonExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLMCBasicTypesTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLOCLExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSetExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
import de.monticore.lang.sysmlv2.types3.SysMLTypeVisitorOperatorCalculator;
import de.monticore.lang.sysmlv2.types3.SysMLWithinScopeBasicSymbolResolver;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class TC3FieldAccessInteroperabilityTest {
  private SysMLv2Tool tool;
  @BeforeAll
  public static void init() {
    LogStub.init();
  }

  @BeforeEach
  public void reset() {
    Log.getFindings().clear();
    tool = new SysMLv2Tool();
    tool.init();

    var type4Ast = new Type4Ast();
    var typeTraverser = SysMLv2Mill.inheritanceTraverser();

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

    var forStreams = new StreamExpressionsTypeVisitor();
    forStreams.setType4Ast(type4Ast);
    typeTraverser.add4StreamExpressions(forStreams);

    var forSets = new SysMLSetExpressionsTypeVisitor();
    forSets.setType4Ast(type4Ast);
    typeTraverser.add4SetExpressions(forSets);

    var forBasicTypes = new SysMLMCBasicTypesTypeVisitor();
    forBasicTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCBasicTypes(forBasicTypes);
    typeTraverser.add4SysMLExpressions(forBasicTypes);

    var forCollectionTypes = new MCCollectionTypesTypeVisitor();
    forCollectionTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCCollectionTypes(forCollectionTypes);

    StreamSymTypeRelations.init();
    SysMLWithinScopeBasicSymbolResolver.init();
    SysMLTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    MCCollectionSymTypeRelations.init();
    SysMLSymTypeRelations.init();

    new SysMLTypeCheck3(typeTraverser, type4Ast).setThisAsDelegate();
  }
  @Test
  public void testInteroperabilityWithoutTCReset() throws IOException {
    var optAst = SysMLv2Mill.parser().parse_String( ""
        + "port def Naturals {\n"
        + "  out attribute channel: nat;\n"
        + "}\n"
        + "\n"
        + "part def LogicBasedConstraints {\n"
        + "  port input: ~Naturals;\n"
        + "  port output: Naturals;\n"
        + "\n"
        + "  constraint streams {\n"
        + "    input == input.channel\n"
        + "  }\n"
        + "}\n");
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var expression = (ASTEqualsExpression)((ASTPartDef)ast.getSysMLElement(1))
        .getSysMLElements(ASTConstraintUsage.class)
        .get(0)
        .getExpression();

    var typeEq = TypeCheck3.typeOf(expression);
    assertTrue(typeEq.isPrimitive() && typeEq.asPrimitive().getPrimitiveName().equals("boolean"));

    var leftNameExpr = (ASTNameExpression)expression.getLeft();
    var typeLeftNameExpr = TypeCheck3.typeOf(leftNameExpr);
    assertEquals("EventStream<nat>", typeLeftNameExpr.print());

    var rightFieldAccess = (ASTFieldAccessExpression)expression.getRight();
    var typeRightFieldAccess = TypeCheck3.typeOf(rightFieldAccess);
    assertEquals("EventStream<nat>", typeRightFieldAccess.print());

    var rightNameExpr = (ASTNameExpression)((ASTFieldAccessExpression)expression.getRight()).getExpression();
    var typeRightNameExpr = TypeCheck3.typeOf(rightNameExpr);
    // this is the side effect of the 2-modes typecheck implementation.
    // Deriving the type of a port usage cannot not reliably be done.
    // Only port usages are affected.
    assertNotEquals("Naturals", typeRightNameExpr.print());
    assertEquals("EventStream<nat>", typeRightNameExpr.print());

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @Test
  public void testInvalidWithoutTCReset() throws IOException {
    var optAst = SysMLv2Mill.parser().parse_String( ""
        + "port def Naturals {\n"
        + "  out attribute channel: nat;\n"
        + "  out attribute channel1: nat;\n"
        + "}\n"
        + "\n"
        + "part def LogicBasedConstraints {\n"
        + "  port input: ~Naturals;\n"
        + "  port output: Naturals;\n"
        + "\n"
        + "  constraint streams {\n"
        + "    input == input.channel\n"
        + "  }\n"
        + "}\n");
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var expression = (ASTEqualsExpression)((ASTPartDef)ast.getSysMLElement(1))
        .getSysMLElements(ASTConstraintUsage.class)
        .get(0)
        .getExpression();

    var typeEq = TypeCheck3.typeOf(expression);
    // in the case there are multiple C&C ports, only field accesses are streams
    assertTrue(typeEq.isObscureType());

    var leftNameExpr = (ASTNameExpression)expression.getLeft();
    var typeLeftNameExpr = TypeCheck3.typeOf(leftNameExpr);
    assertEquals("Naturals", typeLeftNameExpr.print());

    var rightFieldAccess = (ASTFieldAccessExpression)expression.getRight();
    var typeRightFieldAccess = TypeCheck3.typeOf(rightFieldAccess);
    assertEquals("EventStream<nat>", typeRightFieldAccess.print());

    var rightNameExpr = (ASTNameExpression)((ASTFieldAccessExpression)expression.getRight()).getExpression();
    var typeRightNameExpr = TypeCheck3.typeOf(rightNameExpr);
    assertEquals("Naturals", typeRightNameExpr.print());

    assertFalse(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
