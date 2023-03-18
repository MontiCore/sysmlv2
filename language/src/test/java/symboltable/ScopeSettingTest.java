/* (c) https://github.com/MontiCore/monticore */
package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Resolving symbols should be side-effect-free. This class checks this fact!
 */
public class ScopeSettingTest {

  /**
   * Resolves (adapted) symbols from different locations within the symbol table.
   * Checks that scopes and contained symbols do not change as a result.
   *
   * Problem before was:
   * - resolveVariable
   *    - resolveVariableAdapted
   *       - finds and adapts a SysML attribute to VariableSymbol
   *       - sets the enclosingScope of the attribute
   *       - leads to the (newly created) VariableSymbol to be added to the scope
   * - resolveVariable
   *    ... finds two variables (one newly adapted, one previously adapted)!
   */
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
