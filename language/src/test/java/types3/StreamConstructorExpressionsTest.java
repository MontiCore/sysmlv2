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
import de.monticore.lang.sysmlv2.types3.SysMLMCBasicTypesTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLOCLExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSetExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
import de.monticore.lang.sysmlv2.types3.SysMLTypeVisitorOperatorCalculator;
import de.monticore.lang.sysmlv2.types3.SysMLWithinScopeBasicSymbolResolver;
import de.monticore.lang.sysmlv2.types3.SysMLWithinTypeBasicSymbolResolver;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.monticore.types3.util.MapBasedTypeCheck3;
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

public class StreamConstructorExpressionsTest {

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
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<true>} }",
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<false, true, true>} }"
  })
  public void test4validStream(String model) throws IOException {
    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var type = SymTypeRelations.normalize(TypeCheck3.typeOf(expr));
    assertThat(type.printFullName()).isEqualTo("EventStream.EventStream<boolean>");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<true, 5>} }",
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<false, <true> >} }"
  })
  public void test4ValidMixedStream(String model) throws IOException {
    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var type = TypeCheck3.typeOf(expr);

    // Streams are based on union types. So the following expressions are correct, albeit innerly incompatible
    assertTrue(type.isGenericType() && type.asGenericType().getArgument(0).isUnionType());
    var args = type.asGenericType().getArgument(0).asUnionType().getUnionizedTypeSet().toArray();
    assertFalse(SymTypeRelations.isCompatible((SymTypeExpression) args[0], (SymTypeExpression) args[1]));
    assertTrue(Log.getFindings().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<true, 5> == <false>} }",
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<false, <true> > == < <true> >} }"
  })
  public void test4InvalidStream(String model) throws IOException {
    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var type = TypeCheck3.typeOf(expr);

    assertTrue(type.isObscureType());
    assertFalse(Log.getFindings().isEmpty());
  }
}
