package de.monticore.lang.sysml.parser.ownexamples;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class BumperBotTest extends AbstractSysMLTest {

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Test
  public void parseTest(){
    final String pathToDir = "src/test/resources/customexamples" + "/bumperbot";
    SysMLTool.main(new String[]{pathToDir, "-cocosOff"});
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void parseAndCoCosTest(){
    final String pathToDir = "src/test/resources/customexamples" + "/bumperbot";
    SysMLTool.main(new String[]{pathToDir , "-lib=src/main/resources/SysML Domain Libraries"});
    AbstractSysMLTest.printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }
}
