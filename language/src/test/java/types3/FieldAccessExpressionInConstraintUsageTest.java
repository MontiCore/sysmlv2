package types3;

import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.lang.sysmlv2.types3.SysMLCommonExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLOCLExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSetExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
import de.monticore.lang.sysmlv2.types3.SysMLTypeVisitorOperatorCalculator;
import de.monticore.lang.sysmlv2.types3.SysMLWithinScopeBasicSymbolResolver;
import de.monticore.lang.sysmlv2.types3.SysMLWithinTypeBasicSymbolResolver;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldAccessExpressionInConstraintUsageTest {

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

    var forStreams = new StreamExpressionsTypeVisitor();
    forStreams.setType4Ast(type4Ast);
    typeTraverser.add4StreamExpressions(forStreams);

    var forSets = new SysMLSetExpressionsTypeVisitor();
    forSets.setType4Ast(type4Ast);
    typeTraverser.add4SetExpressions(forSets);

    var forBasicTypes = new MCBasicTypesTypeVisitor();
    forBasicTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCBasicTypes(forBasicTypes);

    var forCollectionTypes = new MCCollectionTypesTypeVisitor();
    forCollectionTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCCollectionTypes(forCollectionTypes);

    StreamSymTypeRelations.init();
    SysMLWithinTypeBasicSymbolResolver.init();
    SysMLWithinScopeBasicSymbolResolver.init();
    SysMLTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    MCCollectionSymTypeRelations.init();
    SysMLSymTypeRelations.init();

    new SysMLTypeCheck3(typeTraverser, type4Ast).setThisAsDelegate();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean[2]; } part s { port f: F; constraint e { f.a[1] } }",
      "port def F { attribute a: boolean; } part s { port f: F[1]; constraint e { f[1].a } }",

      "port def F { attribute a: boolean[2]; } part s { port f: F; constraint e { f[1] } }",
      "port def F { attribute a: boolean; } part s { port f: F[1]; constraint e { f[1] } }"
  }) public void test4ValidExpr1(String model) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var type = TypeCheck3.typeOf(expr);
    assertFalse(type.isObscureType());
    assertThat(type.printFullName()).isEqualTo("EventStream.EventStream<boolean>");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean; } part s { port f: F[2]; constraint e { f.a } }",
      "port def F { attribute a: boolean; } part s { port f: F[2]; constraint e { f } }"
  })
  public void test4InvalidExpr(String model) throws IOException {
    var ast = parser.parse_String(model);;
    assertThat(ast).isPresent();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var type = TypeCheck3.typeOf(expr);
    assertTrue(type.isObscureType() ||
        !Log.getFindings().isEmpty() ||
        !type.printFullName().equals("EventStream.EventStream<boolean>"));
  }
}


