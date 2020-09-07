package de.monticore.lang.sysml.cocos;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NamingConventionTest {

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException, IOException {
    LogStub.init();
    Log.getFindings().clear();
  }

  @Test
  public void testValid() {
    SysMLParserForTesting sysMLParserForTesting = new SysMLParserForTesting();
    Optional<ASTUnit> astUnit = sysMLParserForTesting.parseSysML("src/test/resources/examples"
        + "/officialPilotImplementation/2020/03/sysml/src/training/02. Blocks/Blocks Example.sysml");

    NamingConvention coco = new NamingConvention();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit.get()); //TODO write abstract parser
    assertTrue(Log.getFindings().isEmpty());
  }
  @Ignore//TODO
  @Test
  public void testInvalidDoesNotStartWithCaptialLetter() {
    SysMLParserForTesting sysMLParserForTesting = new SysMLParserForTesting();
    Optional<ASTUnit> astUnit = sysMLParserForTesting.parseSysML("src/test/resources/cocos/invalid"
        + "/NamingConvention/Blocks Example.sysml");

    NamingConvention coco = new NamingConvention();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit.get()); //TODO write parser

    Collection<Finding> expectedWarnings = Arrays.asList(
        //Finding.warning("0xSysML04 Name vehicle should start with a capital letter.Blocks Example.sysml:<4,7>")//TODO
        Finding.warning("0xSysML04 Name vehicle should start with a capital letter.",
            new SourcePosition(4, 7, "Blocks Example.sysml"))//TODO
        //  Finding.warning("'%e' Name '%n' should start with a capital letter.Blocks Example.sysml",
        //  new SourcePosition(4, 7))//TODO
        //Finding.warning("'%e' Name '%n' should start with a capital letter.Blocks Example.sysml:<4,7>")//TODO
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
