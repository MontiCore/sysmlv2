package cocos;


import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportsAndPackagesNode;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesCoCoChecker;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.ImportModifierRequiredCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;



import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportModifierRequiredCoCoTest extends NervigeSymboltableTests{

    @BeforeEach
    public void init() {
      LogStub.init();
      SysMLv2Mill.init();
      Log.enableFailQuick(false);
    }

    @Test
    void shouldAcceptImportWithModifier() throws IOException {
      var as = parseImport("public import SomeName;");

      SysMLImportsAndPackagesCoCoChecker checker = new SysMLImportsAndPackagesCoCoChecker();
      checker.addCoCo(new ImportModifierRequiredCoCo());
      checker.checkAll(as);
      assertTrue(Log.getFindings().isEmpty(), "Expected no findings for valid import");
    }

    @Test
    void shouldRejectMissingModifier() throws IOException {
      var as = parseImport("import SomeName;");

      SysMLImportsAndPackagesCoCoChecker checker = new SysMLImportsAndPackagesCoCoChecker();
      checker.addCoCo(new ImportModifierRequiredCoCo());
      checker.checkAll(as);

      assertFalse(Log.getFindings().isEmpty(), "Expected finding for missing modifier");

    }

  private ASTSysMLImportsAndPackagesNode parseImport(String input) throws IOException {
    SysMLv2Parser parser = SysMLv2Mill.parser();

    var result = parser.parse_StringSysMLImportStatement(input); // parses start production -> ASTSysMLImportsAndPackages
   assertFalse(parser.hasErrors(), "Parser errors for: " + input);
   assertTrue(result.isPresent(), "No AST for: " + input);
    return result.get();
  }
}
