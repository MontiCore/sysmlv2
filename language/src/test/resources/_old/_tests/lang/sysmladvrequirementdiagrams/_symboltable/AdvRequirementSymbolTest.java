package de.monticore.lang.sysmladvrequirementdiagrams._symboltable;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2alt.SysMLv2AltMill;
import de.monticore.lang.sysmlv2alt._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2alt._symboltable.ISysMLv2AltGlobalScope;
import de.monticore.lang.sysmlv2alt._symboltable.SysMLv2AltScope;
import de.monticore.lang.sysmlv2alt._visitor.SysMLv2AltTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks successful symbol table creation for adv requirements.
 */
public class AdvRequirementSymbolTest {

  private static ISysMLv2AltGlobalScope scope;

  private static SysMLv2AltTraverser traverser;

  @BeforeAll
  public static void initScope() {
    SysMLv2AltMill.init();
    traverser = SysMLv2AltMill.traverser();
    scope = SysMLv2AltMill.globalScope();
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  /**
   * Tests successful resolving of AdvRequirementDefinition/Usage symbols.
   *
   * @throws IOException
   */
  @Test
  public void testAdvRequirements() throws IOException {
    String model = "src/test/resources/sysmladvrequirementdiagrams/_parser/AdvRequirements.sysml";
    ASTSysMLModel ast = SysMLv2AltMill.parser().parse(model).get();
    SysMLv2AltMill.scopesGenitorDelegator().createFromAST(ast);
    ASTSysMLPackage pkg = (ASTSysMLPackage) ast.getSysMLElement(0);
    assertTrue(((SysMLv2AltScope) pkg.getSpannedScope())
        .resolveAdvRequirementDef("MassPowerLimitationRequirement").isPresent());
    assertTrue(((SysMLv2AltScope) pkg.getSpannedScope())
        .resolveAdvRequirementUsage("massPowerLimitationRequirement").isPresent());
  }
}
