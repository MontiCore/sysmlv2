package de.monticore.lang.sysml.cocos;

import com.sun.xml.internal.bind.v2.TODO;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NamingConventionTest extends AbstractCoCoTest {

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
    ASTUnit astUnit =
        this.parseSysMLSingleModel(this.pathToOfficialSysMLExamples + "/02. Blocks/Blocks Example.sysml");

    NamingConvention coco = new NamingConvention();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidDoesNotStartWithCapitalLetter() {
    ASTUnit astUnit = this.parseSysMLSingleModel(this.pathToInvalidModels
        + "/NamingConvention/Blocks Example.sysml");

    NamingConvention coco = new NamingConvention();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);

    assertEquals(1,Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning("0xSysML04 Name vehicle should start with a capital letter.",
            new SourcePosition(4, 7, "Blocks Example.sysml"))
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
