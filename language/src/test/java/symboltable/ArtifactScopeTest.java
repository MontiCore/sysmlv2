package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2ArtifactScope;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests related to the ArtifactScope
 */
public class ArtifactScopeTest {

  private SysMLv2Tool tool;

  @BeforeAll
  public static void setup() {
    LogStub.init();
  }

  @BeforeEach
  public void init() {
    tool = new SysMLv2Tool();
    tool.init();
  }

  private static Stream<Arguments> modelsAndExpectedNames() {
    return Stream.of(
        arguments("package T { package F; }", "T"),
        arguments("part def T { port f:F; }", "T"),
        arguments("package SomeLongName { package F; }", "SomeLongName")
    );
  }

  /**
   * Artifact scopes have a generated way of determining their name. We check that this works for SysML,
   * were the artifact is assumed to be named after the root element within the document.
   */
  @ParameterizedTest
  @MethodSource({ "modelsAndExpectedNames" })
  public void testArtifactScopeName(String model, String artifactName) throws IOException {
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var artifact = SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    tool.completeSymbolTable(ast);

    assertThat(artifact.getName()).isEqualTo(artifactName);
  }

  /**
   * When there is no clear name, the name should still be set. Otherwise the symbol table and/or resolving
   * could break. The name is set upon first retrieval by {@link SysMLv2ArtifactScope} itself.
   */
  @Test
  public void testIndeterminableArtifactScopeNames() throws IOException {
    var ast = SysMLv2Mill.parser().parse_String("package T; package F;").get();
    var artifact = SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    tool.completeSymbolTable(ast);

    assertThat(artifact.getName()).startsWith("AnonymousArtifact_");
  }

}
