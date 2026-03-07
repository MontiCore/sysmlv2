/* (c) https://github.com/MontiCore/monticore */
package symboltable;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLMetaDataDefinition;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataDefinitionSymbolTest {

  private static final String LOG_NAME = MetadataDefinitionSymbolTest.class.getName();

  @Test
  public void testMetadataDefinitionCreatesTypeSymbol() throws Exception {
    LogStub.init();
    Log.getFindings().clear();

    var tool = new SysMLv2Tool();
    tool.init();

    String model = "package Demo {\n"
        + "  metadata def delayed;\n"
        + "  part def P {\n"
        + "    timing delayed;\n"
        + "  }\n"
        + "}\n";

    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();

    var artifactScope = tool.createSymbolTable(ast.get());

    var pkg = (ASTSysMLPackage) ast.get().getSysMLElement(0);

    var metadataDef = (ASTSysMLMetaDataDefinition) pkg.getSysMLElement(0);
    assertThat(metadataDef.getName()).isEqualTo("delayed");

    assertThat(artifactScope.getSubScopes()).hasSize(1);
    assertThat(artifactScope.getSubScopes().get(0).resolveSysMLMetaDataDefinition("delayed")).isPresent();
    assertThat(artifactScope.getSubScopes().get(0).resolveType("delayed")).isPresent();
  }
}
