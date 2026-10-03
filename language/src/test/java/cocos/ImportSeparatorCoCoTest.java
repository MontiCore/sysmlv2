package cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLQualifiedName;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportsAndPackagesNode;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesCoCoChecker;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.ImportSeparatorCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportSeparatorCoCoTest {

  @BeforeEach
  public void init() {
    SysMLv2Mill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  void shouldAcceptColonColonSeparatedImport() throws IOException {
    Log.getFindings().clear();

    var as = parseImport(
        "public import A::B;");
    SysMLImportsAndPackagesCoCoChecker checker = new SysMLImportsAndPackagesCoCoChecker();
    checker.addCoCo(new ImportSeparatorCoCo());
    checker.checkAll(as);
    assertTrue(Log.getFindings().isEmpty(),
        "Expected no findings for :: separated import");
  }

  @Test
  void shouldRejectDotSeparatedImport() throws IOException {
    Log.getFindings().clear();

    var as = parseImport("public import A.B;"); // negative case
    SysMLImportsAndPackagesCoCoChecker checker = new SysMLImportsAndPackagesCoCoChecker();
    checker.addCoCo(new ImportSeparatorCoCo());
    checker.checkAll(as);
    assertFalse(Log.getFindings().isEmpty(),
        "Expected finding for . separated import");

  }

  // use your already working parseImport(...) from the other test class
  private ASTSysMLImportsAndPackagesNode parseImport(String input)
      throws IOException {
    SysMLv2Parser parser = SysMLv2Mill.parser();

    var result = parser.parse_StringSysMLImportStatement(
        input); // parses start production -> ASTSysMLImportsAndPackages
    assertFalse(parser.hasErrors(), "Parser errors for: " + input);
    assertTrue(result.isPresent(), "No AST for: " + input);
    return result.get();
  }

}
