package de.monticore.lang.sysmlrequirementdiagrams._symboltable;

import de.monticore.lang.sysmlrequirementdiagrams._visitor.RequirementsPostProcessor;
import de.monticore.lang.sysmlv2.SysMLv2Language;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Checks Requirements (Definitions, Usages, etc.)
 * testRequirement1-8 performs the following steps:
 * 1. Parses the model
 * 2. Creates the symbol table
 * 3. Executes RequirementsPostProcessor to enrich requirements AST with info. such as subject type.
 * 4. Validates that each requirement either has no subject or has a 'correct' subject, i.e. follows the
 * compatibility rules.
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

    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse(model);
    assertTrue(ast.isPresent());
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast.get());
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

  /**
   * Creates a SysMLModel by performing the following steps:
   * 1. parsing
   * 2. symbol table creation
   * 3. postprocessing
   *
   * @param model String
   * @return ASTSysMLModel
   * @throws IOException
   */
  private ASTSysMLModel getModel(String model) throws IOException {
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Language.getPreSymbolTableCoCoChecker().checkAll(ast);
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    SysMLv2Language.getPostSymbolTableCoCoChecker().checkAll(ast);
    ast.accept(traverser);
    return ast;
  }

  /**
   * Retrieves the RequirementSubjectSymbol from the given scope, if present.
   *
   * @param scope ISysMLRequirementDiagramsScope
   * @return Optional<RequirementSubjectSymbol>
   */
  private Optional<RequirementSubjectSymbol> getSubject(ISysMLRequirementDiagramsScope scope) {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    if (scope.getRequirementSubjectSymbols().size() > 0) {
      subject = Optional.ofNullable(scope.getRequirementSubjectSymbols().values().get(0));
    }
    return subject;
  }

  /**
   * Given a name, resolves for RequirementDefSymbol in a given scope.
   * NOTE: Assumes that the symbol does exist.
   *
   * @param scope       ISysMLRequirementDiagramsScope
   * @param requirement String
   * @return RequirementDefSymbol
   */
  private RequirementDefSymbol getRequirementDef(ISysMLRequirementDiagramsScope scope, String requirement) {
    return scope.resolveRequirementDef(requirement).get();
  }

  /**
   * Given a name, resolves for RequirementUsageSymbol in a given scope.
   * NOTE: Assumes that the symbol does exist.
   *
   * @param scope       ISysMLRequirementDiagramsScope
   * @param requirement String
   * @return RequirementUsageSymbol
   */
  private RequirementUsageSymbol getRequirementUsage(ISysMLRequirementDiagramsScope scope, String requirement) {
    return scope.resolveRequirementUsage(requirement).get();
  }

  /**
   * Given a RequirementSubjectSymbol and a subject type, validates the symbol does have the same subject type.
   *
   * @param subject     RequirementSubjectSymbol
   * @param subjectType String
   */
  private void validateSubjectType(RequirementSubjectSymbol subject, String subjectType) {
    assertEquals(subjectType, subject.getSubjectType().getTypeInfo().getName());
  }

  @Test
  public void testRequirement1() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_1.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementUsageSymbol requirementGroup = getRequirementUsage(packageScope, "requirementGroup");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementGroup.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    RequirementUsageSymbol nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(),
        "nestedRequirement_1");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "Engine");

    nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(), "nestedRequirement_2");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "Bolt");
  }

  @Test
  public void testRequirement2() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_2.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementUsageSymbol requirementGroup = getRequirementUsage(packageScope, "requirementGroup");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementGroup.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    RequirementUsageSymbol nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(),
        "nestedRequirement_1");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "engine");

    nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(), "nestedRequirement_2");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "bolt");
  }

  @Test
  public void testRequirement3() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_3.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementUsageSymbol requirementGroup = getRequirementUsage(packageScope, "requirementGroup");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementGroup.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    RequirementUsageSymbol nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(),
        "nestedRequirement_1");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "Engine");

    nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(), "nestedRequirement_2");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "Bolt");
  }

  @Test
  public void testRequirement4() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_4.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementUsageSymbol requirementGroup = getRequirementUsage(packageScope, "requirementGroup");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementGroup.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    RequirementUsageSymbol nestedRequirement = getRequirementUsage(requirementGroup.getSpannedScope(),
        "nestedRequirement");
    subject = getSubject(nestedRequirement.getSpannedScope());
    validateSubjectType(subject.get(), "engine");
  }

  @Test
  public void testRequirement5() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_5.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementUsageSymbol requirementUsage = getRequirementUsage(packageScope, "requirementUsage");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "boolean");
  }

  @Test
  public void testRequirement6() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_6.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementDefSymbol requirementDef = getRequirementDef(packageScope, "NoBodyRequirement");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementDef.getSpannedScope());
    assertFalse(subject.isPresent());

    requirementDef = getRequirementDef(packageScope, "RequirementWithoutSubject");
    subject = getSubject(requirementDef.getSpannedScope());
    assertFalse(subject.isPresent());

    requirementDef = getRequirementDef(packageScope, "SpecializedRequirementWithDefinedSubject");
    subject = getSubject(requirementDef.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    RequirementUsageSymbol requirementUsage = getRequirementUsage(packageScope, "noBodyRequirement");
    subject = getSubject(requirementUsage.getSpannedScope());
    assertFalse(subject.isPresent());

    requirementUsage = getRequirementUsage(packageScope, "requirementWithoutSubject");
    subject = getSubject(requirementUsage.getSpannedScope());
    assertFalse(subject.isPresent());

    requirementUsage = getRequirementUsage(packageScope, "requirementWithSubjectDefined");
    subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    requirementUsage = getRequirementUsage(packageScope, "requirementWithSubject");
    subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    requirementUsage = getRequirementUsage(packageScope, "requirementWithSubjectRedefined");
    subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "Car");
  }

  @Test
  public void testRequirement7() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_7.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementDefSymbol requirementDef = getRequirementDef(packageScope, "RequirementWithSubject");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementDef.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    requirementDef = getRequirementDef(packageScope, "SpecializedRequirementWithRedefinedSubject");
    subject = getSubject(requirementDef.getSpannedScope());
    validateSubjectType(subject.get(), "Car");

    RequirementUsageSymbol requirementUsage = getRequirementUsage(packageScope, "requirementWithSubject1");
    subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    requirementUsage = getRequirementUsage(packageScope, "requirementWithSubject2");
    subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "Car");

    requirementUsage = getRequirementUsage(packageScope, "requirementWithSpecializedSubject");
    subject = getSubject(requirementUsage.getSpannedScope());
    validateSubjectType(subject.get(), "FlyingCar");
  }

  @Test
  public void testRequirement8() throws IOException {
    ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_8.sysml");
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    RequirementDefSymbol requirementDef = getRequirementDef(packageScope, "RequirementWithSubject");
    Optional<RequirementSubjectSymbol> subject = getSubject(requirementDef.getSpannedScope());
    validateSubjectType(subject.get(), "Vehicle");

    RequirementUsageSymbol requirementGroup = getRequirementUsage(packageScope, "requirementGroup");
    subject = getSubject(requirementGroup.getSpannedScope());
    assertFalse(subject.isPresent());

    RequirementUsageSymbol requirementUsage = getRequirementUsage(requirementGroup.getSpannedScope(),
        "nestedRequirement1InGroup");
    subject = getSubject(requirementUsage.getSpannedScope());
    assertFalse(subject.isPresent());

    requirementDef = getRequirementDef(packageScope, "RequirementWithoutSubjectWithNestedRequirements");
    subject = getSubject(requirementDef.getSpannedScope());
    assertFalse(subject.isPresent());

    requirementDef = getRequirementDef(packageScope, "RequirementWithSubjectWithNestedRequirements");
    subject = getSubject(requirementDef.getSpannedScope());
    validateSubjectType(subject.get(), "Car");
  }

  @Test
  public void testRequirement9() throws IOException {
    try {
      Log.enableFailQuick(false);
      Log.clearFindings();
      ASTSysMLModel ast = getModel("src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_9.sysml");
      String error1 = Log.getFindings().get(0).toString();
      String error2 = Log.getFindings().get(1).toString();
      String error3 = Log.getFindings().get(2).toString();
      String error4 = Log.getFindings().get(3).toString();
      String error5 = Log.getFindings().get(4).toString();
      assertEquals("Multiple requirement subjects found. At most one subject is allowed in requirement!", error1);
      assertEquals("Subject of nested requirement 'Vehicle' is not compatible with the subject of the " +
          "corresponding enclosing requirement! Enclosing requirement doesn't define a subject.", error2);
      assertEquals("Subject of requirement usage is not compatible with the subject of the " +
          "corresponding enclosing requirement!", error3);
      assertEquals("Subjects found to be incompatible with one another!", error4);
      assertEquals("Specialized requirement definition 'SpecializedRequirement' "
          + "with subject type 'Engine' is not compatible with inherited subject type 'Vehicle'!", error5);
    }
    finally {
      Log.clearFindings();
      Log.enableFailQuick(true);
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
    testScopeNesting(
        "src/test/resources/sysmlrequirementdiagrams/_symboltable/RequirementDefinitionSymbol_Unnamed.sysml");
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
    SymTypeExpression subjectType = reqDef.getSpannedScope().getRequirementSubjectSymbols().values().get(
        0).getSubjectType();
    assertEquals("Vehicle", subjectType.getTypeInfo().getName());

    RequirementUsageSymbol reqUsage = reqDef.getSpannedScope().resolveRequirementUsage("boltRequirement").get();
    subjectType = reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0).getSubjectType();
    assertEquals("Bolt", subjectType.getTypeInfo().getName());

    reqUsage = packageScope.resolveRequirementUsage("carRequirement").get();
    subjectType = reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0).getSubjectType();
    assertEquals("Engine", subjectType.getTypeInfo().getName());
  }
}
