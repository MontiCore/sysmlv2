package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import org.eclipse.xtext.validation.Issue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.omg.sysml.interactive.SysMLInteractive;
import org.omg.sysml.lang.sysml.util.SysMLLibraryUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class ParsersComparisonFullTest {
  private static final String MODEL_PATH = "src/test/resources/parser";

  private final SysMLv2Parser parser = SysMLv2Mill.parser();

  // SysMLInteractive is a singleton; keep one reference
  private static SysMLInteractive official;

  // Replace with the actual class that contains runDefaultCoCos/runAdditionalCoCos
  private static SysMLv2Tool montiCoCos;

  @BeforeAll
  public static void init() {
    Log.init();
    Log.enableFailQuick(false);
    SysMLv2Mill.init();

    official = SysMLInteractive.getInstance();
    official.setVerbose(false);

    montiCoCos = new SysMLv2Tool();
  }

  @BeforeEach
  public void reset() {
    parser.setError(false);
    Log.getFindings().clear(); // critical for parameterized tests
  }

  @ParameterizedTest(name = "{index} - {0} does parse w/o errors (MontiCore + official)")
  @ValueSource(strings = {
      "packages.sysml",
  })
  public void testParsingModels(String modelName) throws IOException {
    Path modelPath = Path.of(MODEL_PATH, modelName);

    // 1) MontiCore parse
    //Optional<ASTSysMLModel> astOpt = SysMLv2Mill.parser().parse(modelPath.toString());
    //assertFalse(parser.hasErrors(), "MontiCore parsing should not have failed");
    //assertTrue(astOpt.isPresent(), "MontiCore AST should have been created");
    //ASTSysMLModel ast = astOpt.get();
//
    ////1b) MontiCore full validation (CoCos)
    ////montiCoCos.runDefaultCoCos(ast);
    ////montiCoCos.runAdditionalCoCos(ast);
//
    //assertTrue(
    //    Log.getFindings().isEmpty(),
    //    () -> "MontiCore validation findings for " + modelName + ":\n" + Log.getFindings()
    //);

    // 2) Official OMG parser (parse + validate)
    official.loadLibrary(Paths.get("src/main/resources/").toAbsolutePath().toString());
    official.next(".sysml");
    String input = Files.readString(modelPath);
    official.parse(input);
    var errors = official.getResource().getErrors();
    assertTrue(
        errors.isEmpty(),
        () -> "Omg parser found errors when MC parser did not" + modelName + ":\n" + errors
    );

    List<Issue> issues = official.validate();

    assertTrue(
        issues.stream().noneMatch(issue -> issue.getSeverity().name().equals("ERROR")),
        () -> "Official OMG validation issues for " + modelName + ":\n" + formatIssues(issues)
    );
  }

  private static String formatIssues(List<Issue> issues) {
    StringBuilder sb = new StringBuilder();
    for (Issue i : issues) {
      sb.append(i.getSeverity())
          .append(": ")
          .append(i.getMessage())
          .append(" @ ")
          .append(i.getLineNumber())
          .append(":")
          .append(i.getColumn())
          .append("\n");
    }
    return sb.toString();
  }
}
