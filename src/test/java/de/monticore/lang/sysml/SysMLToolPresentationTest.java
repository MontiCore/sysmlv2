package de.monticore.lang.sysml;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.sysmlnamesbasis._ast.ResolveQualifiedNameHelper;
import de.monticore.lang.sysml.basics.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLToolPresentationTest extends AbstractSysMLTest {

  private final boolean presentationMode = true;
  private final String pathToSrcDir = "src/test/resources/customexamples/presentation";

  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    if (!presentationMode) {
      this.setUpLog();
    }
  }

  @Test
  @Ignore // TODO fix me
  public void toolShowSysMLToolForPresentationTest() {
    List<ASTUnit> models = SysMLTool.mainForJava(new String[] {
        pathToSrcDir + "/vehicles",
        "-lib=" + pathToSrcDir + "/library" });
    //Deactivate CoCos with -cocosOff

    assertEquals(3, models.size());

    ArrayList<String> qualifiedNameToSeats = new ArrayList<String>(
        Arrays.asList(
            "automobile",
            "vehicle",
            "seats"));

    List<SysMLTypeSymbol> packageWithImportsSymbol = ResolveQualifiedNameHelper.
        resolveQualifiedNameAsListInASpecificScope(qualifiedNameToSeats,
            models.get(0).getEnclosingScope().getEnclosingScope()); // This is the global scope
    assertEquals(1, packageWithImportsSymbol.size());


    ArrayList<String> qualifiedNameWithDotCarSeat = new ArrayList<String>(
        Arrays.asList(
            "mySeats",
            "Car.Seat"));

    List<SysMLTypeSymbol> qualifiedNameWithDotCarSeatSymbol = ResolveQualifiedNameHelper.
        resolveQualifiedNameAsListInASpecificScope(qualifiedNameWithDotCarSeat,
            models.get(0).getEnclosingScope().getEnclosingScope());
    assertEquals(1, qualifiedNameWithDotCarSeatSymbol.size());

    ArrayList<String> qualifiedNameWithDotCarSeatWRONG = new ArrayList<String>(
        Arrays.asList(
            "mySeats",
            "Car","Seat"));

    List<SysMLTypeSymbol> qualifiedNameWithDotCarSeatWRONGSymbol = ResolveQualifiedNameHelper.
        resolveQualifiedNameAsListInASpecificScope(qualifiedNameWithDotCarSeatWRONG,
            models.get(0).getEnclosingScope().getEnclosingScope());
    assertEquals(0, qualifiedNameWithDotCarSeatWRONGSymbol.size());


    assertEquals(0, Log.getErrorCount());
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning("0xA7156 Could not resolve import \"wrongImport\".",
            new SourcePosition(4, 1, "automobile.sysml")),
        Finding.warning("0xA7150 Name \"vehicle\" should start with a capital letter.",
            new SourcePosition(6, 7, "automobile.sysml")),
        Finding.warning("0xA7155 package \"mySeats\" should be equal to the Filename.",
            new SourcePosition(1, 0, "seats.sysml")));
    Assert.assertErrors(expectedWarnings, Log.getFindings());


  }

}
