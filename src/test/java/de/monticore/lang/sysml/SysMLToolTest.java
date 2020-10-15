package de.monticore.lang.sysml;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

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
    final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";
    SysMLTool.main(new String[]{pathToDir + "/training/", "-cocosOff"});
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithLibDirTest(){
    final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";
    SysMLTool.main(new String[]{pathToDir + "/training/",
      "-lib=src/main/resources/SysML Domain Libraries", "-cocosOff"});
    //AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().size() == 8); //TODO addToScope warning
  }

  @Test
  public void toolParseAndCheckAllTrainingExamplesWithLibDirCoCosTest(){
    final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";
    SysMLTool.main(new String[]{pathToDir + "/training/",
      "-lib=src/main/resources/SysML Domain Libraries"});
    //AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().size() == 38);
        //TODO addToScope warning, equal to the filename, imports of libs
  }
}
