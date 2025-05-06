package symboltable;

import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnonymousUsageTest extends NervigeSymboltableTests {

  @Test
  public void testAnonymousUsageInTransition() throws IOException {
    ISysMLv2ArtifactScope artifactScope = this.process(
        FileUtils.readFileToString(
            new File("./src/test/resources/symboltable/complexTransition.sysml"),
            StandardCharsets.UTF_8)
    );

    tool.runDefaultCoCos((ASTSysMLModel) artifactScope.getAstNode());
    assertEquals(0, LogStub.getErrorCount(), String.join("\n", LogStub.getPrints()));
  }
}
