package types3;

import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.lang.sysmlv2.types3.SysMLCommonExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLOCLExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLWithinScopeBasicSymbolResolver;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>This test is about TypeCheck3 deriving the types of FieldAccessExpressions in StateUsages.</p>
 *
 * <p>When ASTFieldAccessExpression and ASTOCLArrayQualification are in StateUsage
 * we test whether they are not calculated as Stream such as in constraints.</p>
 */
public class FieldAccessExpressionInStateUsageTest {

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

    new MapBasedTypeCheck3(typeTraverser, type4Ast).setThisAsDelegate();
  }

  static Stream<Arguments> createInputs() {
    return Stream.of(
        Arguments.of(
            "port def F { attribute a: boolean; }" +
                "part def X { port f: F; state s { transition first S if f then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean[3]; }" +
                "part def X { port f: F; state s { transition first S if f[1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1] then S; } }")
        ,Arguments.of(
           "port def F { attribute a: boolean[3]; } " +
               "part def X { port f: F[3]; exhibit state s { transition first S if f[1][1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; } part def X { port f: F; constraint e { f } }")


        ,Arguments.of(
            "port def F { attribute a: boolean; }" +
                "part def X { port f: F; state s { transition first S if f.a then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean[3]; }" +
                "part def X { port f: F; state s { transition first S if f.a[1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1].a then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean[3]; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1].a[1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; } part def X { port f: F; constraint e { f.a } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; } part def X { port f: F[3]; constraint e { f[1].a } }")

        ,Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; }" +
                "part def X { port f: F; state s { transition first S if f.a then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean[3]; attribute b: nat[3]; }" +
                "part def X { port f: F; state s { transition first S if f.a[1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1].a then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean[3]; attribute b: nat[3]; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1].a[1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; } part def X { port f: F; constraint e { f.a } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; } part def X { port f: F[3]; constraint e { f[1].a } }")
    );
  }

  static Stream<Arguments> createInvalidInputs() {
    return Stream.of(
        Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; }" +
                "part def X { port f: F; state s { transition first S if f then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean[3]; attribute b: nat[3]; }" +
                "part def X { port f: F; state s { transition first S if f[1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; } " +
                "part def X { port f: F[3]; exhibit state s { transition first S if f[1] then S; } }")
        ,Arguments.of(
           "port def F { attribute a: boolean[3]; attribute b: nat[3]; } " +
               "part def X { port f: F[3]; exhibit state s { transition first S if f[1][1] then S; } }")
        ,Arguments.of(
            "port def F { attribute a: boolean; attribute b: nat; } part def X { port f: F; constraint e { f } }")
        // TODO one field access with a stream method
    );
  }

  @ParameterizedTest
  @MethodSource({ "createInputs" })
  public void test(String model) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysmlmodel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysmlmodel);
    tool.completeSymbolTable(astSysmlmodel);
    var astPartdef = astSysmlmodel.getSysMLElementList().get(1);
    var astSysmlelement = ((ASTPartDef) astPartdef).getSysMLElement(1);
    if (astSysmlelement instanceof ASTStateUsage) {
      var astTransition = ((ASTStateUsage) astSysmlelement).getSysMLElement(0);
      var expr = ((ASTSysMLTransition) astTransition).getGuard();
      var type = TypeCheck3.typeOf(expr);
      assertThat(type.printFullName()).isEqualTo("boolean");
    } else if (astSysmlelement instanceof ASTConstraintUsage) {
      var expr = ((ASTConstraintUsage) astSysmlelement).getExpression();
      var type = TypeCheck3.typeOf(expr);
      assertThat(type.printFullName()).isEqualTo(
          "EventStream.EventStream<boolean>");
    }
    else {
      Assertions.fail("ASTSysMLElement should here be ASTStateUsage");
    }
  }

  @ParameterizedTest
  @MethodSource({ "createInvalidInputs" })
  public void testInvalid(String model) throws IOException {
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    var astSysmlmodel = ast.get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(astSysmlmodel);
    tool.completeSymbolTable(astSysmlmodel);
    var astPartdef = astSysmlmodel.getSysMLElementList().get(1);
    var astSysmlelement = ((ASTPartDef) astPartdef).getSysMLElement(1);
    SymTypeExpression type = SymTypeExpressionFactory.createObscureType();

    Log.enableFailQuick(false);

    if (astSysmlelement instanceof ASTStateUsage) {
      var astTransition = ((ASTStateUsage) astSysmlelement).getSysMLElement(0);
      var expr = ((ASTSysMLTransition) astTransition).getGuard();
      type = TypeCheck3.typeOf(expr);
    } else if (astSysmlelement instanceof ASTConstraintUsage) {
      var expr = ((ASTConstraintUsage) astSysmlelement).getExpression();
      type = TypeCheck3.typeOf(expr);
    }
    else {
      Assertions.fail("ASTSysMLElement should here be ASTStateUsage");
    }

    assertTrue(!type.isPrimitive() || !type.asPrimitive().getPrimitiveName().equals("boolean"));
    Log.enableFailQuick(true);
  }
}
