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

public class CompareGenericTypesTest {

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
    SysMLWithinScopeBasicSymbolResolver.init();
    SysMLTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    MCCollectionSymTypeRelations.init();
    SysMLSymTypeRelations.init();

    new SysMLTypeCheck3(typeTraverser, type4Ast).setThisAsDelegate();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "input.val == <true>",
      "input.val == <true> implies not(input.val == <false>)",
      "input.val == inputMc.val",
      "inputInt.val == < 5 >",
      "< 4 , true> == < 2 , false>",
  })
  public void test4CompatibleGenericTypes(String expression) throws IOException {
    var type = typeOfConstraintExpression(expression);

    System.out.println("TypeCheck calculated: " + type.getTypeInfo().getFullName());
    assertTrue(type.isPrimitive());
    assertThat(type.printFullName()).isEqualTo("boolean");
    System.out.println("Checking Log after TypeCheck...");
    for (var finding : Log.getFindings()) {
      System.out.println(finding);
    }
    assertTrue(Log.getFindings().isEmpty());
    System.out.println("Done");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "input.val == true",
      "inputInt.val == 5 ",
      "input.val == inputInt.val",
      "inputInt.val == < 5 , true>",
  })
  public void test4IncompatibleGenericTypes(String expression) throws IOException {
    var type = typeOfConstraintExpression(expression);

    System.out.println("TypeCheck calculated: " +
      (type.isObscureType() ? "ObscureType" : type.getTypeInfo().getFullName())
    );
    assertTrue(type.isObscureType());
    System.out.println("Checking Log after TypeCheck...");
    for (var finding : Log.getFindings()) {
      System.out.println(finding);
    }
    assertFalse(Log.getFindings().isEmpty());
    System.out.println("Done");
  }

  protected de.monticore.types.check.SymTypeExpression typeOfConstraintExpression(
      String expression) throws IOException {
    var model =
        "private import ScalarValues::Boolean;" +
        "port def Booleans { in attribute val: Boolean; }" +
        "port def McBooleans { in attribute val: boolean; }" +
        "port def Integers { in attribute val: int; }" +
        "part def myPart { " +
          "port input: Booleans;" +
          "port inputMc: McBooleans;" +
          "port inputInt: Integers;" +
          "assert constraint e {" +
            expression +
          "}" +
        "}"
        ;

    var ast = parser.parse_String(model);

    System.out.println("Parsing model...");
    assertThat(ast).isPresent();
    System.out.println("Checking Log after parsing...");
    for (var finding : Log.getFindings()) {
      System.out.println(finding);
    }
    assertThat(Log.getFindings()).isEmpty();

    var astSysMLModel = ast.get();
    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartDef = sysmlelements.get(4);
    var constraintUsage = ((ASTPartDef) astPartDef).getSysMLElement(3);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();

    System.out.println("TypeChecking...");
    return TypeCheck3.typeOf(expr);
  }
}
