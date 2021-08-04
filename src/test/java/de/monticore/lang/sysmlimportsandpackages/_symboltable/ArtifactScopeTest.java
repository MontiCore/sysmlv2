/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.lang.sysmlimportsandpackages.SysMLImportsAndPackagesMill;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class ArtifactScopeTest {

  private static ISysMLImportsAndPackagesArtifactScope scope;

  @BeforeClass
  public static void init() {
    SysMLImportsAndPackagesMill.init();
    scope = SysMLImportsAndPackagesMill.artifactScope();
  }

  @Test
  public void testName_IsNotPresent() {
    assertFalse("ArtifactScopes should not have a name", scope.isPresentName());
  }

  @Test
  public void testRemainingNamesForResolveDown_PassThrough() {
    String name = "dawd45a3w4d68425awd.dawdadw::dwd454";
    assertEquals(
            "RemainingNamesForResolveDown should just pass the name through",
            scope.getRemainingNameForResolveDown(name),
            name);
  }

}
