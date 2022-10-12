package symboltable;

import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializationTest {

  private static final String BASE = "src/test/resources/symboltable/serialization";

  public static Stream<Arguments> createInputs() {
    return Stream.of(
        Arguments.of(
            "Port Usages",
            BASE + "/portUsage/PortDefinitions.sysml",
            BASE + "/portUsage/PortDefinitionsReference.sysml",
            BASE + "/portUsage/PortDefinitionsExpected.sym",
            BASE + "/portUsage/PortDefinitions.sym",
            "specification.BooleanInput",
            "BooleanInput",
            "specification.Inverter.i"
        ),
        Arguments.of(
            "State Exhibition",
            BASE + "/exhibitState/StateDef.sysml",
            BASE + "/exhibitState/StateDefReference.sysml",
            BASE + "/exhibitState/StateDefExpected.sym",
            BASE + "/exhibitState/StateDef.sym",
            "automata.AutomatonInverter",
            "AutomatonInverter",
            "automata.Inverter.i"
        ),
        Arguments.of(
            "Constraint Refinement",
            BASE + "/refinement/constraint/RoughPartDef.sysml",
            BASE + "/refinement/constraint/RoughPartDefReference.sysml",
            BASE + "/refinement/constraint/RoughPartDefExpected.sym",
            BASE + "/refinement/constraint/RoughPartDef.sym",
            "refinement.B",
            "B",
            "A"
        ),
        Arguments.of(
            "StateDef Refinement",
            BASE + "/refinement/stateDef/RoughStateDef.sysml",
            BASE + "/refinement/stateDef/RoughStateDefReference.sysml",
            BASE + "/refinement/stateDef/RoughStateDefExpected.sym",
            BASE + "/refinement/stateDef/RoughStateDef.sym",
            "avionic.UnfairMedium",
            "UnfairMedium",
            "PerfectMedium"
        )
    );
  }

  private final static SysMLv2Tool sysmlLang = new SysMLv2Tool();

  @BeforeAll
  public static void setPrinter() {
    JsonPrinter.enableIndentation();
  }

  @BeforeEach
  public void setUpSymboltable() {
    sysmlLang.init();
  }

  @ParameterizedTest(name = "Serializing {0}")
  @MethodSource("createInputs")
  public void serialize(
      String name,
      String modelPath,
      String modelReferencePath,
      String expectedSymboltablePath,
      String symboltablePath,
      String fqnSymbol,
      String relativeSymbol,
      String usageSymbol) throws IOException
  {
    ASTSysMLModel ast = sysmlLang.parse(modelPath);
    ISysMLv2ArtifactScope firstArtifactScope = sysmlLang.createSymbolTable(ast);
    sysmlLang.completeSymbolTable(ast);

    try {
      sysmlLang.storeSymbols(firstArtifactScope, symboltablePath);

      assertTrue(FileUtils.contentEqualsIgnoreEOL(Paths.get(symboltablePath).toFile(), Paths.get(
          expectedSymboltablePath).toFile(), "UTF-8"));
    }
    finally {
      //cleanUp
      Files.deleteIfExists(Paths.get(symboltablePath));
    }
  }

  @ParameterizedTest(name = "Deserializing {0}")
  @MethodSource("createInputs")
  public void loadSymbolTableAndResolve(
      String name,
      String modelPath,
      String modelReferencePath,
      String expectedSymboltablePath,
      String symboltablePath,
      String fqnSymbol,
      String relativeSymbol,
      String usageSymbol)
  {
    ASTSysMLModel ast = sysmlLang.parse(modelReferencePath);
    sysmlLang.createSymbolTable(ast);
    sysmlLang.completeSymbolTable(ast);

    SysMLv2GlobalScope gs = (SysMLv2GlobalScope) sysmlLang.getGlobalScope();

    gs.addSubScope(gs.getSymbols2Json().load(expectedSymboltablePath));

    // check if an additional ArtifactScope was found
    Assertions.assertThat(gs.getSubScopes()).size().isGreaterThan(1);

    // check inter-model resolution of fqn from usage package scope.
    ISysMLv2Scope packageScope = gs.getSubScopes().get(0).getSubScopes().get(0);
    Optional<SysMLTypeSymbol> resolved = packageScope.resolveSysMLType(fqnSymbol);
    assertThat(resolved).isPresent();

    if(resolved.get() instanceof PortDefSymbol) {
      // check if symbolrule type matches resolved type
      PortUsageSymbol portUsage = packageScope.resolvePortUsage(usageSymbol).get();
      assertThat(portUsage.getTypes(0).printFullName()).isEqualTo(resolved.get().getFullName());
    }

    // resolution of relative symbols does not work if symbol is in another artifact scope (i.e. top-down resolution is required).
    Optional<SysMLTypeSymbol> relativeResolved = packageScope.resolveSysMLType(relativeSymbol);
    assertThat(relativeResolved).isEmpty();
  }
}