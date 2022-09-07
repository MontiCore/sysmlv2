/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlblockdiagrams._symboltable;

import de.monticore.lang.sysmlparametrics._symboltable.ConstraintUsageSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks PortUsageSymbols
 */
public class PortUsageSymbolTest {

  private static final String MODEL = "src/test/resources/sysmlblockdiagrams/_symboltable/PortUsageSymbol.sysml";

  private static ISysMLv2GlobalScope scope;

  @BeforeAll
  public static void initScope() throws IOException {
    SysMLv2Mill.init();
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(MODEL).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    scope = SysMLv2Mill.globalScope();
  }

  /**
   * Tests resolving "example.SignalConverter.o" down from the global scope using PortUsageSymbol.sysml
   */
  @Test
  public void testResolveDown() {
    Optional<SysMLPortUsageSymbol> portUsage = scope.resolveSysMLPortUsage("example.SignalConverter.o");
    assertTrue(portUsage.isPresent(), "PortUsage could not be resolved from the global scope");
  }

  /**
   * Tests resolving "o" up from the constraint's spanned symbol (i.e., from inside the body) using PortUsageSymbol.sysml
   */
  @Test
  public void testResolveFromReference() {
    ConstraintUsageSymbol constraint = scope.resolveConstraintUsage("example.SignalConverter.equality").get();
    Optional<SysMLPortUsageSymbol> portUsage = constraint.getSpannedScope().resolveSysMLPortUsage("o");
    assertTrue(portUsage.isPresent(), "PortUsage could not be resolved from the body of the constraint");
  }

}
