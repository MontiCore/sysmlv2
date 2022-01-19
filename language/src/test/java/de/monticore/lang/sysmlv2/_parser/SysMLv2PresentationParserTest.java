package de.monticore.lang.sysmlv2._parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the parser with custom examples based on the slide set ({@link SysMLv2SlidesParserTest})
 */
public class SysMLv2PresentationParserTest {

  private static final String MODEL_PATH = "src/test/resources/sysmlv2/parser/presentation/";

  @Test
  public void testLibraryBolts() throws IOException {
    Path model = Paths.get(MODEL_PATH + "library/bolts.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testVehicleAutomobile() throws IOException {
    Path model = Paths.get(MODEL_PATH + "vehicles/automobile.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testVehicleSeats() throws IOException {
    Path model = Paths.get(MODEL_PATH + "vehicles/seats.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

}
