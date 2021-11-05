package de.monticore.lang.sysmlrequirementdiagrams._symboltable;


import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Checks RequirementDefinitionSymbols
 */
public class RequirementDefinitionTest {

  @Before
  public void initScope() {
    SysMLv2Mill.init();
    SysMLv2Mill.globalScope().clear();
  }

  private void testScopeNesting(String model) throws IOException {
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2GlobalScope scope = SysMLv2Mill.globalScope();

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
    In case of unnamed constraint usages inside of requirement defintions, no new scope is being spanned for the
    constraint usage. It sets the scope spanned by the requirement as its current scope in call to visit in:
        SysMLParametricsScopesGenitor.java:162,
    this is consequently removed in call to endVisit in:
        SysMLParametricsScopesGenitor.java:202.
    The scope that is actually being removed from stack here is the package scope, which is why when it comes to setting
    the scope for the second requirement definition, it is added in the artifact scope.
     */
    testScopeNesting("src/test/resources/sysmlrequirementdiagrams/_symboltable/RequirementDefinitionSymbol_Unnamed.sysml");
  }

}
