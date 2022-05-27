package sysml4verificationLanguage;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testet, ob Requirement Definitionen ausgelesen werden können
 */
public class RequirementTest {

  public static Stream<Arguments> createInputs() {
    String BASE = "src/test/resources";
    return Stream.of(
        Arguments.of("Requirement State Invariant", BASE + "/sysml4verification/parser/requirement_state_invariant.sysml")
    );
  }

  @ParameterizedTest(name = "run #{index} with [{arguments}]")
  @MethodSource("createInputs")
  public void testLanguage(String name, String modelLocation) throws IOException {

    List<RequirementDefSymbol> componentSymbols = SysML4VerificationLanguage.findRequirementDefinitionsIn(
        SysML4VerificationLanguage.getGlobalScopeFor(Path.of(modelLocation))
    );

    assertThat(componentSymbols).hasSize(1);
    assertEquals("MyStateInvariant", componentSymbols.get(0).getName());
  }

}
