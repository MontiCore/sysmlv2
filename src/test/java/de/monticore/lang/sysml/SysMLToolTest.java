package de.monticore.lang.sysml;

import de.monticore.cocos.helper.Assert;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLToolTest {
  private final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src";

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
    SysMLTool.main(new String[]{pathToDir + "/training/"});
  }
}
