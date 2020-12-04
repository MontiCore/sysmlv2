package de.monticore.lang.sysml;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
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


  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void checkFailOnZeroArgs(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error("Please specify only one single path to the input directory containing the input models.")
    );

    SysMLTool.main(new String[]{});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
  @Test
  public void checkFailOnMultipleArgs(){
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error("Please specify only one single path to the input directory containing the input models.")
    );

    SysMLTool.main(new String[]{"arg1", "arg2"});
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }

  @Test
  public void toolParseAndCheckAllTrainingExamples(){
    final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";
    SysMLTool.main(new String[]{pathToDir + "/training/"});
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }
}
