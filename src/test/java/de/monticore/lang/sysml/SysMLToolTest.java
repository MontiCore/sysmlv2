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

  private String wrongUsageError =
      "Please specify one single path to the input directory containing the input models."
          + "\n - Optional: Add a directory for libraries with -lib=<path>"
          + "\n - Optional: Turn off Context Conditions with -cocosOff";

  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  private final String pathToSrcDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";
  private final String pathToLibDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/"
      + "library/Domain Libraries";
  @Test
  public void checkFailOnZeroArgs(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(wrongUsageError)
    );

    SysMLTool.main(new String[]{});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
  @Test
  public void checkFailOnTwoArgs(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(wrongUsageError)
    );

    SysMLTool.main(new String[]{"arg1", "arg2"});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void checkFailOnThreeArgs(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(wrongUsageError)
    );

    SysMLTool.main(new String[]{"arg1", "arg2", "arg3"});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void checkFailOnThreeArgsOnlyOneOption1(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(wrongUsageError)
    );

    SysMLTool.main(new String[]{"arg1", "arg2", "-cocosOff"});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
  @Test
  public void checkFailOnThreeArgsOnlyOneOption2(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(wrongUsageError)
    );

    SysMLTool.main(new String[]{"arg1", "arg2", "-lib=something..."});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesCoCosOffTest(){
    SysMLTool.main(new String[]{ pathToSrcDir + "/training/", "-cocosOff"});
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithLibDirTest(){
    SysMLTool.main(new String[]{ pathToSrcDir + "/training/",
      "-lib=src/main/resources/SysML Domain Libraries", "-cocosOff"});
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithLibDirCoCosTest(){

    SysMLTool.main(new String[]{ pathToSrcDir + "/training/",
      "-lib="+pathToLibDir}); //Same files as in src/main/resources/SysML Domain Libraries but for testing.
    //AbstractSysMLTest.printAllFindings();
    //assertEquals(36, Log.getFindings().size());
    for (Finding f:Log.getFindings()) { //not equal to filename coco, double definition (e.g. mm) at SI
      boolean filenameCoCo = f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsFileName)));
      boolean doubleImport = f.toString().contains(
          SysMLCoCos.getErrorCode((SysMLCoCoName.ImportedElementNameAlreadyExists)));
      assertTrue(filenameCoCo || doubleImport);
      if(!(filenameCoCo || doubleImport)){
        System.out.println("Did not expect the Finding:" + f.toString() );
      }
    }
  }
  @Test
  public void toolParseAndCheckAllTrainingExamplesWithMultipleLibDirCoCosTest(){
    SysMLTool.main(new String[]{ pathToSrcDir + "/training/",
      "-lib="+pathToLibDir+"/Geometry",
        "-lib="+pathToLibDir+"/Quantities and Units"
    });
    //AbstractSysMLTest.printAllFindings();
    //assertEquals(36, Log.getFindings().size());
    for (Finding f:Log.getFindings()) { //not equal to filename coco, double definition (e.g. mm) at SI
      boolean filenameCoCo = f.toString().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsFileName)));
      boolean doubleImport = f.toString().contains(
          SysMLCoCos.getErrorCode((SysMLCoCoName.ImportedElementNameAlreadyExists)));
      assertTrue(filenameCoCo || doubleImport);
      if(!(filenameCoCo || doubleImport)){
        System.out.println("Did not expect the Finding:" + f.toString() );
      }
    }
  }
}
