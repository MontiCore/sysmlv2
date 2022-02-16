package schrotttests;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.lang.sysml.ad._ast.ASTParameterListStd;
import de.monticore.lang.sysml.ad._ast.ASTParameterMemberStd;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTBehaviorUsageMemberAssertConstraintUsage;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTBehaviorUsageMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTFeatureTyping;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTRootNamespace;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTNestedUsageMemberStd;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTParameterStd;
import de.monticore.lang.sysml4verification._ast.ASTClassifierDeclarationCompletionVerification;
import de.monticore.lang.sysml4verification._ast.ASTFeatureTypingVerification;
import de.monticore.lang.sysml4verification._ast.ASTInfinity;
import de.monticore.lang.sysml4verification._parser.SysML4VerificationParser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PfsTest {

  /**
   * Makes sure all Pilot Flying System (PFS) components parse
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "code/Bus", "code/EventBus",
      "hlrprime/Clock", "hlrprime/Side", "hlrprime/Bus", "hlrprime/Redundancy",
      "hlr/FairClock"
  })
  public void testParse(String model) throws IOException {
    Optional<ASTUnit> optAst = new SysML4VerificationParser().parse("src/test/resources/pfs/" + model + ".sysml");
    assertThat(optAst).isNotEmpty();
  }

  /**
   * Tests the parameters for "part def"/"block"
   */
  @Test
  public void testBlockParameter() throws IOException {
    Optional<ASTUnit> optAst = new SysML4VerificationParser().parse("src/test/resources/pfs/code/Bus.sysml");

    ASTPackagedDefinitionMember partDef = ((ASTRootNamespace)optAst.get()).getPackage(0).getPackageBody()
        .getPackageMember(4).getPackagedDefinitionMember();
    assertThat(partDef).isInstanceOf(ASTBlock.class);

    ASTParameterMemberStd parameter = ((ASTParameterMemberStd)((ASTParameterListStd)(
        (ASTClassifierDeclarationCompletionVerification)((ASTBlock)partDef).getBlockDeclaration()
            .getClassifierDeclarationCompletion()).getParameterList()).getParameterMember(0));
    assertThat(parameter.getName()).isEqualTo("initialSide");

    ASTFeatureTyping typing = ((ASTParameterStd)parameter.getParameter()).getParameterTypePart().getFeatureTyping();
    assertThat(typing).isInstanceOf(ASTFeatureTypingVerification.class);

    String type = ((ASTFeatureTypingVerification)typing).getMCType()
        .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    assertThat(type).isEqualTo("Boolean");
  }

  /**
   * Testet ad-hoc Contraint-Instance-Creation, Expressions und INF-Literale
   */
  @Test
  public void testContraintExpression() throws IOException {
    Optional<ASTUnit> optAst = new SysML4VerificationParser()
        .parse("src/test/resources/pfs/hlrprime/Clock.sysml");

    ASTPackagedDefinitionMember partDef = ((ASTRootNamespace)optAst.get()).getPackage(0).getPackageBody()
        .getPackageMember(1).getPackagedDefinitionMember();

    ASTBehaviorUsageMember constraint = ((ASTNestedUsageMemberStd)((ASTDefinitionBodyStd)((ASTBlock)partDef)
        .getDefinitionBody()).getNestedUsageMember(1)).getBehaviorUsageMember();
    assertThat(constraint).isInstanceOf(ASTBehaviorUsageMemberAssertConstraintUsage.class);

    ASTExpression expression = ((ASTBehaviorUsageMemberAssertConstraintUsage)constraint).getAssertConstraintUsage().getConstraintBody()
        .getConstraintMembers().getConstraintExpressionMember().getExpression();
    assertThat(expression).isInstanceOf(ASTEqualsExpression.class);
    assertThat(((ASTEqualsExpression)expression).getLeft()).isInstanceOf(ASTCallExpression.class);
    assertThat(((ASTEqualsExpression)expression).getRight()).isInstanceOf(ASTLiteralExpression.class);
    assertThat(((ASTLiteralExpression)((ASTEqualsExpression)expression).getRight()).getLiteral())
        .isInstanceOf(ASTInfinity.class);
  }

  @Test
  public void testInfinityLiteral() throws IOException {
    Optional<ASTUnit> optAst = new SysML4VerificationParser()
        .parse("src/test/resources/pfs/hlrprime/Clock.sysml");

    ASTPackagedDefinitionMember partDef = ((ASTRootNamespace)optAst.get()).getPackage(0).getPackageBody()
        .getPackageMember(1).getPackagedDefinitionMember();

    // validate positive INF literal

    ASTBehaviorUsageMember constraint = ((ASTNestedUsageMemberStd)((ASTDefinitionBodyStd)((ASTBlock)partDef)
        .getDefinitionBody()).getNestedUsageMember(1)).getBehaviorUsageMember();
    ASTExpression expression = ((ASTBehaviorUsageMemberAssertConstraintUsage)constraint).getAssertConstraintUsage().getConstraintBody()
        .getConstraintMembers().getConstraintExpressionMember().getExpression();
    assertThat(((ASTEqualsExpression)expression).getRight()).isInstanceOf(ASTLiteralExpression.class);
    assertThat(((ASTLiteralExpression)((ASTEqualsExpression)expression).getRight()).getLiteral())
        .isInstanceOf(ASTInfinity.class);
    assertThat(!((ASTInfinity)((ASTLiteralExpression)((ASTEqualsExpression)expression).getRight()).getLiteral()).isNegative());

    // validate negative INF literal

    constraint = ((ASTNestedUsageMemberStd)((ASTDefinitionBodyStd)((ASTBlock)partDef)
        .getDefinitionBody()).getNestedUsageMember(2)).getBehaviorUsageMember();
    assertThat(constraint).isInstanceOf(ASTBehaviorUsageMemberAssertConstraintUsage.class);

    expression = ((ASTBehaviorUsageMemberAssertConstraintUsage)constraint).getAssertConstraintUsage().getConstraintBody()
        .getConstraintMembers().getConstraintExpressionMember().getExpression();
    assertThat(((ASTNotEqualsExpression)expression).getRight()).isInstanceOf(ASTLiteralExpression.class);
    assertThat(((ASTLiteralExpression)((ASTNotEqualsExpression)expression).getRight()).getLiteral())
        .isInstanceOf(ASTInfinity.class);
    assertThat(((ASTInfinity)((ASTLiteralExpression)((ASTNotEqualsExpression)expression).getRight()).getLiteral()).isNegative());
  }
}
