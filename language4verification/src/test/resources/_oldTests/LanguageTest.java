package schrotttests;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification._symboltable.BlockSymbol;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Testet die "Nutzung der Sprache", dh. das klassische "gib mir Komponente X.Y.Z aus dem GlobalScope an diesem Ort".
 */
public class LanguageTest {

  public static Stream<Arguments> createInputs() {
    String BASE = "src/test/resources";
    return Stream.of(
        Arguments.of("Simple Automata and Specifications", BASE + "/language/simple"),
        Arguments.of("Pilot Flying System Case Study", BASE + "/language/pfs"),
        Arguments.of("Event-based Ampel Case Study", BASE + "/language/event"),
        Arguments.of("Refinement Between Automata", BASE + "/language/refinement")
    );
  }

  @ParameterizedTest(name = "run #{index} with [{arguments}]")
  @MethodSource("createInputs")
  public void testLanguage(String name, String modelLocation) throws IOException {

    List<BlockSymbol> componentSymbols = SysML4VerificationLanguage.findPartDefinitionsIn(
        SysML4VerificationLanguage.getGlobalScopeFor(Path.of(modelLocation))
    );

  }

}
