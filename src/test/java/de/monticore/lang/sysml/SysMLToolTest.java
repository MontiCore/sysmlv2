package de.monticore.lang.sysml;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLToolTest {

  private String wrongUsageError = "Please specify one single path to the input directory containing the input models."
      + "\n - Optional: Add directories for libraries with -lib=<path>"
      + "\n - Optional: Turn off Context Conditions with -cocosOff";

  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  private final String pathToSrcDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";
  private final String pathToLibDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/" +
      "library/Domain Libraries";

  @Test
  public void checkFailOnZeroArgs() {
    Collection<Finding> expectedErrors = Arrays.asList(Finding.error(wrongUsageError));

    SysMLTool.main(new String[] {});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void checkFailOnTwoArgs() {
    Collection<Finding> expectedErrors = Arrays.asList(Finding.error(wrongUsageError));

    SysMLTool.main(new String[] { "arg1", "arg2" });
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void checkFailOnThreeArgs() {
    Collection<Finding> expectedErrors = Arrays.asList(Finding.error(wrongUsageError));

    SysMLTool.main(new String[] { "arg1", "arg2", "arg3" });
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void checkFailOnThreeArgsOnlyOneOption1() {
    Collection<Finding> expectedErrors = Arrays.asList(Finding.error(wrongUsageError));

    SysMLTool.main(new String[] { "arg1", "arg2", "-cocosOff" });
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void checkFailOnThreeArgsOnlyOneOption2() {
    Collection<Finding> expectedErrors = Arrays.asList(Finding.error(wrongUsageError));

    SysMLTool.main(new String[] { "arg1", "arg2", "-lib=something..." });
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesCoCosOffTest() {
    SysMLTool.main(new String[] { pathToSrcDir + "/training/", "-cocosOff" });
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithLibDirTest() {
    SysMLTool.main(new String[] { pathToSrcDir + "/training/", "-lib=src/main/resources/SysML Domain Libraries",
        "-cocosOff" });
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithLibDirCoCosTest() {

    SysMLTool.main(new String[] { pathToSrcDir + "/training/", "-lib=" + pathToLibDir }); //Same files as in
    // src/main/resources/SysML Domain Libraries but for testing.
    //AbstractSysMLTest.printAllFindings();
    //System.out.println("Found " + Log.getFindings().size() + " findings.");
    //assertEquals(36, Log.getFindings().size());
    for (Finding f : Log.getFindings()) { //not equal to filename coco, double definition (e.g. mm) at SI
      boolean filenameCoCo = f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsArtifactName)));
      boolean doubleImport =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.ImportedElementNameAlreadyExists)));
      boolean twoImportsWithDifferentSymbolButSameName =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.ImportDifferentSymbolsWithDuplicateName)));
      boolean resolveQualifiedName =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.NameReference)));
      boolean namingConvention =
          (f.toString().contains(
              SysMLCoCos.getErrorCode(SysMLCoCoName.UsageNameStartsWithLowerCase)+
                  " Name \"Vehicle c1 Design Context\" should start with a lower "
                  + "case letter.")
          );
      assertTrue("Did not expect the Finding:" + f.toString(), filenameCoCo || doubleImport
          ||twoImportsWithDifferentSymbolButSameName || resolveQualifiedName ||namingConvention);
      assertTrue(!f.isError()); // Do not throw errors for the official examples
    }
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithMultipleLibDirCoCosTest() {
    SysMLTool.main(new String[] { pathToSrcDir + "/training/", "-lib=" + pathToLibDir + "/Geometry",
        "-lib=" + pathToLibDir + "/Quantities and Units" });

    for (Finding f : Log.getFindings()) {
      boolean filenameCoCo = f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsArtifactName)));
      boolean doubleImport =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.ImportedElementNameAlreadyExists)));
      boolean twoImportsWithDifferentSymbolButSameName =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.ImportDifferentSymbolsWithDuplicateName)));
      boolean resolveQualifiedName =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.NameReference)));
      boolean namingConvention =
          (f.toString().contains(
              SysMLCoCos.getErrorCode(SysMLCoCoName.UsageNameStartsWithLowerCase)+
                  " Name \"Vehicle c1 Design Context\" should start with a lower "
                  + "case letter.")
          );

      assertTrue("Did not expect the Finding: " + f.toString(), filenameCoCo || doubleImport
        ||twoImportsWithDifferentSymbolButSameName || resolveQualifiedName || namingConvention);
      assertTrue(!f.isError()); // Do not throw errors for the official examples
    }
  }

  @Test
  public void toolParseAndCheckAllMainExamplesWithMultipleLibDirCoCosTest() {
    SysMLTool.main(new String[] { pathToSrcDir + "/examples/", "-lib=" + pathToLibDir + "/Geometry",
        "-lib=" + pathToLibDir + "/Quantities and Units" });
    // We do not consider CoCos here, because the CoCos restrict the official models, which leads to many
    // CoCo violations. This method just tests, if any errors are thrown (e.g., when isPresent is not called before
    // the get method).
    /*for (Finding f : Log.getFindings()) {
      boolean filenameCoCo = f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsFileName)));
      boolean doubleImport =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.ImportedElementNameAlreadyExists)));
      assertTrue("Did not expect the Finding:" + f.toString(), filenameCoCo || doubleImport);
    }*/
  }
  @Test
  public void toolParseAndCheckAllValidationExamplesWithMultipleLibDirCoCosTest() {
    SysMLTool.main(new String[] { pathToSrcDir + "/validation/", "-lib=" + pathToLibDir + "/Geometry",
        "-lib=" + pathToLibDir + "/Quantities and Units" });
    // We do not consider CoCos here, because the CoCos restrict the official models, which leads to many
    // CoCo violations. This method just tests, if any errors are thrown (e.g., when isPresent is not called before
    // the get method).
    /*for (Finding f : Log.getFindings()) {
      boolean filenameCoCo = f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsFileName)));
      boolean doubleImport =
          f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.ImportedElementNameAlreadyExists)));
      assertTrue("Did not expect the Finding:" + f.toString(), filenameCoCo || doubleImport);
    }*/
  }
}
