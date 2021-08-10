/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.lang.sysmlimportsandpackages.SysMLImportsAndPackagesMill;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class ArtifactScopeTest {

  private static ISysMLv2ArtifactScope scope;

  @BeforeClass
  public static void init() {
    SysMLImportsAndPackagesMill.init();
    scope = SysMLv2Mill.artifactScope();
  }

  @Test
  public void testName_IsNotPresent() {
    assertFalse("ArtifactScopes should not have a name", scope.isPresentName());
  }

  /**
   * ArtifactScopes are transparent to the resolving process. They should just "pass through" any names they receive
   * when resolving down.
   */
  @Test
  public void testRemainingNamesForResolveDown_PassThrough() {
    String name = "dawd45a3w4d68425awd.dawdadw::dwd454";
    List<String> remaining = scope.getRemainingNameForResolveDown(name);
    assertEquals("RemainingNamesForResolveDown should just pass the name through", 1, remaining.size());
    assertEquals("RemainingNamesForResolveDown should just pass the name through", name, remaining.get(0));
  }

}
