package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Language;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SymboltableTest {

  private static final String BASE = "src/test/resources/sysmlv2/_symboltable/serialization";

  public static Stream<Arguments> createInputs() {
    return Stream.of(
        Arguments.of(
            "Port Usages",
            BASE + "/portUsage/PortDefinitions.sysml",
            BASE + "/portUsage/PortDefinitionsReference.sysml",
            BASE + "/portUsage/PortDefinitionsExpected.sym",
            BASE + "/portUsage/PortDefinitions.sym",
            "specification.BooleanInput",
            "BooleanInput"
        ),
        Arguments.of(
            "State Exhibition",
            BASE + "/exhibitState/StateDef.sysml",
            BASE + "/exhibitState/StateDefReference.sysml",
            BASE + "/exhibitState/StateDefExpected.sym",
            BASE + "/exhibitState/StateDef.sym",
            "automata.AutomatonInverter",
            "AutomatonInverter"
        ),
        Arguments.of(
            "Constraint Refinement",
            BASE + "/refinement/constraint/RoughPartDef.sysml",
            BASE + "/refinement/constraint/RoughPartDefReference.sysml",
            BASE + "/refinement/constraint/RoughPartDefExpected.sym",
            BASE + "/refinement/constraint/RoughPartDef.sym",
            "refinement.B",
            "B"
        ),
        Arguments.of(
            "StateDef Refinement",
            BASE + "/refinement/stateDef/RoughStateDef.sysml",
            BASE + "/refinement/stateDef/RoughStateDefReference.sysml",
            BASE + "/refinement/stateDef/RoughStateDefExpected.sym",
            BASE + "/refinement/stateDef/RoughStateDef.sym",
            "avionic.UnfairMedium",
            "UnfairMedium"
        )
    );
  }

  @BeforeAll
  public static void setPrinter() {
    JsonPrinter.enableIndentation();
  }

  @BeforeEach
  public void setUpSymboltable() {
    SysMLv2Mill.init();
    SysMLv2Mill.globalScope().clear();
  }

  @ParameterizedTest(name = "Serializing {0}")
  @MethodSource("createInputs")
  public void serialize(String name, String modelPath, String modelReferencePath, String expectedSymboltablePath,
                        String symboltablePath, String fqnSymbol, String relativeSymbol) throws IOException {
    SysMLv2GlobalScope initialSymbolTable = (SysMLv2GlobalScope) SysMLv2Language
        .getGlobalScopeFor(Paths.get(modelPath));

    List<ISysMLv2Scope> artifactScopes = initialSymbolTable.getSubScopes();

    SysMLv2Symbols2Json s2j = initialSymbolTable.getSymbols2Json();

    try {
      s2j.store((SysMLv2ArtifactScope) artifactScopes.get(0), symboltablePath);

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
  public void loadSymbolTableAndResolve(String name, String modelPath, String modelReferencePath, String expectedSymboltablePath,
                                        String symboltablePath, String fqnSymbol, String relativeSymbol) throws IOException {
    SysMLv2GlobalScope initialSymbolTable = (SysMLv2GlobalScope) SysMLv2Language
        .getGlobalScopeFor(Paths.get(modelReferencePath), Paths.get(expectedSymboltablePath).getParent());

    // check inter-model resolution of fqn from usage package scope. This loads stored STs if Symbol is not found until GlobalScope
    ISysMLv2Scope packageScope = initialSymbolTable.getSubScopes().get(0).getSubScopes().get(0);
    Optional<SysMLTypeSymbol> resolved =  packageScope.resolveSysMLType(fqnSymbol);
    assertThat(resolved).isPresent();

    // check if an additional ArtifactScope was found
    Assertions.assertThat(initialSymbolTable.getSubScopes()).size().isGreaterThan(1);

    // resolution of relative symbols does not work if symbol is in another artifact scope (i.e. top-down resolution is required).
    Optional<SysMLTypeSymbol> relativeResolved = packageScope.resolveSysMLType(relativeSymbol);
    assertThat(relativeResolved).isEmpty();
  }
}
