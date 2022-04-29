package sysml4verificationLanguage;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testet die "Nutzung der Sprache", dh. das klassische "gib mir Komponente X.Y.Z aus dem GlobalScope an diesem Ort".
 */
public class LanguageTest {

  public static Stream<Arguments> createInputs() {
    String BASE = "src/test/resources";
    return Stream.of(
        Arguments.of("Double Inverter", BASE + "/simple/automaton/")
    );
  }

  @ParameterizedTest(name = "run #{index} with [{arguments}]")
  @MethodSource("createInputs")
  public void testLanguage(String name, String modelLocation) throws IOException {

    List<PartDefSymbol> componentSymbols = SysML4VerificationLanguage.findPartDefinitionsIn(
        SysML4VerificationLanguage.getGlobalScopeFor(Path.of(modelLocation))
    );

    assertThat(componentSymbols).hasSize(2);
  }

}
