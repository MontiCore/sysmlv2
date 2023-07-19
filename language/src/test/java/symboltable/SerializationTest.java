/* (c) https://github.com/MontiCore/monticore */
package symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
            BASE + "/portUsage/PortDefinitions.sym",
            BASE + "/portUsage/PortDefinitionsActual.sym",
            "PortDefinitions.BooleanInput",
            "BooleanInput",
            "PortDefinitionsReference.Inverter.i"
        ),
        Arguments.of(
            "State Exhibition",
            BASE + "/exhibitState/StateDef.sysml",
            BASE + "/exhibitState/StateDefReference.sysml",
            BASE + "/exhibitState/StateDef.sym",
            BASE + "/exhibitState/StateDefActual.sym",
            "StateDef.AutomatonInverter",
            "AutomatonInverter",
            "StateDefReference.Inverter.i"
        ),
        Arguments.of(
            "Constraint Refinement",
            BASE + "/refinement/constraint/RoughPartDef.sysml",
            BASE + "/refinement/constraint/RoughPartDefReference.sysml",
            BASE + "/refinement/constraint/RoughPartDef.sym",
            BASE + "/refinement/constraint/RoughPartDefActual.sym",
            "RoughPartDef.B",
            "B",
            "A"
        ),
        Arguments.of(
            "StateDef Refinement",
            BASE + "/refinement/stateDef/RoughStateDef.sysml",
            BASE + "/refinement/stateDef/RoughStateDefReference.sysml",
            BASE + "/refinement/stateDef/RoughStateDef.sym",
            BASE + "/refinement/stateDef/RoughStateDefActual.sym",
            "RoughStateDef.UnfairMedium",
            "UnfairMedium",
            "PerfectMedium"
        )
    );
  }

  private final static SysMLv2Tool sysmlTool = new SysMLv2Tool();

  @BeforeAll
  public static void setPrinter() {
    JsonPrinter.enableIndentation();
  }

  @BeforeEach
  public void setUpSymboltable() {
    sysmlTool.init();
  }

  @ParameterizedTest(name = "Serializing {0}")
  @MethodSource("createInputs")
  public void serialize(
      String name,
      String modelPath,
      String modelReferencePath,
      String symboltablePathExpected,
      String symboltablePathActual,
      String fqnSymbol,
      String relativeSymbol,
      String usageSymbol) throws IOException
  {
    ASTSysMLModel ast = sysmlTool.parse(modelPath);
    ISysMLv2ArtifactScope firstArtifactScope = sysmlTool.createSymbolTable(ast);
    sysmlTool.completeSymbolTable(ast);

    try {
      sysmlTool.storeSymbols(firstArtifactScope, symboltablePathActual);

      assertTrue(FileUtils.contentEqualsIgnoreEOL(Paths.get(symboltablePathActual).toFile(), Paths.get(
          symboltablePathExpected).toFile(), "UTF-8"));
    }
    finally {
      //cleanUp
      Files.deleteIfExists(Paths.get(symboltablePathActual));
    }
  }

  @ParameterizedTest(name = "Deserializing {0}")
  @MethodSource("createInputs")
  public void loadSymbolTableAndResolve(
      String name,
      String modelPath,
      String modelReferencePath,
      String symboltablePathExpected,
      String symboltablePathActual,
      String fqnSymbol,
      String relativeSymbol,
      String usageSymbol)
  {
    ASTSysMLModel ast = sysmlTool.parse(modelReferencePath);
    sysmlTool.createSymbolTable(ast);

    var symbolPath = new MCPath();
    // MCPath entries correspond to the directories that contain symboltables
    symbolPath.addEntry(Paths.get(symboltablePathExpected).getParent());
    var gs = sysmlTool.getGlobalScope();
    gs.setSymbolPath(symbolPath);

    // Completion requires the capability of loading symbols
    sysmlTool.completeSymbolTable(ast);

    // check if an additional ArtifactScope was found during completion (Specialization completion uses inter-model resolution)
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

  @Test
  public void testRefinmentReferenceSerialization() throws IOException {
    var modelReferencePath = BASE + "/partDefWithRefinement/Refinement.sysml";
    var symboltablePathExpected = BASE + "/partDefWithRefinement/Refinement.sym";
    var symboltablePathActual = BASE + "/partDefWithRefinement/RefinementActual.sym";

    ASTSysMLModel ast = sysmlTool.parse(modelReferencePath);
    ISysMLv2ArtifactScope firstArtifactScope = sysmlTool.createSymbolTable(ast);
    sysmlTool.completeSymbolTable(ast);
    sysmlTool.finalizeSymbolTable(ast);

    try {
      sysmlTool.storeSymbols(firstArtifactScope, symboltablePathActual);

      assertTrue(FileUtils.contentEqualsIgnoreEOL(Paths.get(symboltablePathActual).toFile(), Paths.get(
          symboltablePathExpected).toFile(), "UTF-8"));
    }
    finally {
      //cleanUp
      Files.deleteIfExists(Paths.get(symboltablePathActual));
    }
  }

  @Test
  public void testRefinmentReferenceDeSerialization(){
    var modelReferencePath = BASE + "/partDefWithRefinement/RefinementReference.sysml";
    var symboltablePathExpected = BASE + "/partDefWithRefinement/Refinement.sym";
    var symboltablePathActual = BASE + "/partDefWithRefinement/RefinementActual.sym";
    var fqnSymbol = "Refinement.PerfectMedium";

    ASTSysMLModel ast = sysmlTool.parse(modelReferencePath);
    sysmlTool.createSymbolTable(ast);

    var symbolPath = new MCPath();
    // MCPath entries correspond to the directories that contain symboltables
    symbolPath.addEntry(Paths.get(symboltablePathExpected).getParent());
    var gs = sysmlTool.getGlobalScope();
    gs.setSymbolPath(symbolPath);

    sysmlTool.completeSymbolTable(ast);
    sysmlTool.finalizeSymbolTable(ast);

    Optional<PartDefSymbol> resolved = gs.resolvePartDef(fqnSymbol);

    assertThat(resolved).isPresent();
    assertThat(resolved.get().getDirectRefinements()).size().isEqualTo(1);
    assertThat(resolved.get().getTransitiveRefinements()).size().isEqualTo(2);
  }
}
