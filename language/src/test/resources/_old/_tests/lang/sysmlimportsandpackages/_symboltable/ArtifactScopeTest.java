/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.lang.sysmlimportsandpackages.SysMLImportsAndPackagesMill;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtifactScopeTest {

  private static ISysMLv2ArtifactScope scope;

  @BeforeAll
  public static void init() {
    SysMLImportsAndPackagesMill.init();
    scope = SysMLv2Mill.artifactScope();
  }

  @Test
  public void testName_IsNotPresent() {
    assertFalse(scope.isPresentName(), "ArtifactScopes should not have a name");
  }

  /**
   * ArtifactScopes are transparent to the resolving process. They should just "pass through" any names they receive
   * when resolving down.
   */
  @Test
  public void testRemainingNamesForResolveDown_PassThrough() {
    String name = "dawd45a3w4d68425awd.dawdadw::dwd454";
    List<String> remaining = scope.getRemainingNameForResolveDown(name);
    assertEquals(1, remaining.size(), "RemainingNamesForResolveDown should just pass the name through");
    assertEquals(name, remaining.get(0), "RemainingNamesForResolveDown should just pass the name through");
  }

}
