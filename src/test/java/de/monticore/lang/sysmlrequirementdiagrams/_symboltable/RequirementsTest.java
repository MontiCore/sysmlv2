package de.monticore.lang.sysmlrequirementdiagrams._symboltable;


import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._visitor.RequirementsPostProcessor;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.lang.sysmlrequirementdiagrams.RequirementDiagramsHelper.getSubjectType;
import static junit.framework.TestCase.assertEquals;

/**
 * Checks Requirements (Definitions, Usages, etc.)
 */
public class RequirementsTest {

  private static ISysMLv2GlobalScope scope;
  private static SysMLv2Traverser traverser;

  @BeforeClass
  public static void initScope() {
    SysMLv2Mill.init();
    traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLRequirementDiagrams(new RequirementsPostProcessor());
    scope = SysMLv2Mill.globalScope();
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  private void testScopeNesting(String model) throws IOException {
    // The nature of test demands a reinitalized mill (to verify scope nesting,
    // we make remove traces of previously ran tests in the suite)
    SysMLv2Mill.reset();
    initScope();

    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    scope = SysMLv2Mill.globalScope();

    // global scope only has one subscope, i.e. artifact scope
    assertEquals(1, scope.getSubScopes().size());

    // artifact scope only has one subscope, i.e. package scope
    assertEquals(1, scope.getSubScopes().get(0).getSubScopes().size());

    ISysMLv2Scope pkgScope = scope.getSubScopes().get(0).getSubScopes().get(0);

    // package scope has two subscopes, each per requirement definition
    assertEquals(2, pkgScope.getSubScopes().size());

    // both req. defs have subscope each (scope spanned by constraint usage defined in requirement body)
    assertEquals(1, pkgScope.getSubScopes().get(0).getSubScopes().size());
    assertEquals(1, pkgScope.getSubScopes().get(1).getSubScopes().size());
  }

  private void checkASTRequirement(ISysMLv2Scope scope, String spaces) {
    for (ISysMLv2Scope subscope : scope.getSubScopes()) {
      // Look at RequirementDefinition
      if (subscope.getAstNode() instanceof ASTRequirementDef) {
        ASTRequirementDef requirementDef = ((ASTRequirementDef) subscope.getAstNode());
        System.out.println("RequirementDefinition: " + subscope.getName());
        if (requirementDef.getSpannedScope().getRequirementSubjectSymbols().size() == 0) {
          System.out.println("No subject defined, subject can be anything!");
        } else {
          SymTypeExpression subjectType = getSubjectType(requirementDef.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
          System.out.println(spaces + "Subject: " + subjectType.getTypeInfo().getName());
        }
      }
      // Look at RequirementUsage
      else if (subscope.getAstNode() instanceof ASTRequirementUsage) {
        System.out.println(spaces + "RequirementUsage: " + subscope.getName());
        ASTRequirementUsage requirementUsage = ((ASTRequirementUsage) subscope.getAstNode());
        if (requirementUsage.getSpannedScope().getRequirementSubjectSymbols().size() == 0) {
          System.out.println(spaces + "No subject defined, subject can be anything!");
        } else {
          SymTypeExpression subjectType = getSubjectType(requirementUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
          System.out.println(spaces + "Subject: " + subjectType.getTypeInfo().getName());
        }
      }
      checkASTRequirement(subscope, spaces + "\t");
    }
  }

  /**
   * testValidRequirements performs the following steps:
   * 1. Parses the model
   * 2. Creates the symbol table
   * 3. Executes RequirementsPostProcessor to enrich requirements AST with info. such as subject type.
   * 4. Validates that each requirement either has no subject or has a 'correct' subject, i.e. follows the
   * compatibility rules.
   *
   * @throws IOException
   */
  @Test
  public void testValidRequirements() throws IOException {
    String[] models = {
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_1.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_2.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_3.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_4.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_5.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_6.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_7.sysml",
            "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_8.sysml",
    };
    for (String model : models) {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      ast.accept(traverser);
      ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);
      checkASTRequirement(packageScope, "");
    }
  }

  /**
   * Tests that scopes are nested correctly
   */
  @Test
  public void testNestedScopes() throws IOException {
    testScopeNesting("src/test/resources/sysmlrequirementdiagrams/_symboltable/RequirementDefinitionSymbol.sysml");
  }

  /**
   * Tests that scopes are nested correctly
   */
  @Test
  public void testNestedScopes_UnnamedCase() throws IOException {
    /*
    In case of unnamed constraint usages inside of requirement definitions, no new scope is being spanned for the
    constraint usage. It sets the scope spanned by the requirement as its current scope in call to visit in:
        SysMLParametricsScopesGenitor.java:162,
    this is consequently removed in call to endVisit in:
        SysMLParametricsScopesGenitor.java:202.
    The scope that is actually being removed from stack here is the package scope, which is why when it comes to setting
    the scope for the second requirement definition, it is added in the artifact scope.
     */
    testScopeNesting("src/test/resources/sysmlrequirementdiagrams/_symboltable/RequirementDefinitionSymbol_Unnamed.sysml");
  }

  /**
   * Following test verifies that type check computer the correct type for the requirement subjects.
   *
   * @throws IOException
   */
  @Test
  public void testRequirementSubjectType() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/RequirementsTypeCheck.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(traverser);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementDefSymbol reqDef = packageScope.resolveRequirementDef("VehicleRequirement").get();
    SymTypeExpression subjectType = getSubjectType(reqDef.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
    assertEquals("Vehicle", subjectType.getTypeInfo().getName());

    RequirementUsageSymbol reqUsage = reqDef.getSpannedScope().resolveRequirementUsage("boltRequirement").get();
    subjectType = getSubjectType(reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
    assertEquals("Bolt", subjectType.getTypeInfo().getName());

    reqUsage = packageScope.resolveRequirementUsage("carRequirement").get();
    subjectType = getSubjectType(reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
    assertEquals("Engine", subjectType.getTypeInfo().getName());
  }
}
