package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scope trees and symbols have to be invariant to resolution inside ArtifactScopes.
 * This test checks if adapted type-resolution methods have side effects on scope-spanning symbols.
 */
public class ScopeSettingTest {
  @Test
  public void testScopeSetting() throws IOException {
    var sysmlTool = new SysMLv2Tool();
    sysmlTool.init();

    var ast = SysMLv2Mill.parser().parse_String("package MyPackage { port def MyTypeAsPortDef {} }");

    var artScope = sysmlTool.createSymbolTable(ast.get());
    // ST completion was omitted to not avoid unnecessary resolve calls

    var portDefScope = artScope.getSubScopes().get(0).getSubScopes().get(0);
    var portDefName = portDefScope.getSpanningSymbol().getName();

    var scopeSpanningSymbolBefore = portDefScope.getSpanningSymbol();

    var resolved = portDefScope.resolveType(portDefName);
    assertTrue(resolved.isPresent());

    var scopeSpanningSymbolAfter = portDefScope.getSpanningSymbol();

    assertEquals(scopeSpanningSymbolBefore.getClass().getName(),
        scopeSpanningSymbolAfter.getClass().getName());
  }
}
