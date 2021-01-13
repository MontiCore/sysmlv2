package de.monticore.lang.sysml.cocos.naming;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class DefinitionNameStartsWithCapitalLetterTest extends AbstractSysMLTest {

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }
  @Test
  public void testValid() {
    ASTUnit astUnit =
        this.parseSysMLSingleModel(this.pathToOfficialSysMLTrainingExamples + "/02. Blocks/Blocks Example.sysml");
    DefinitionNameStartsWithCapitalLetter coco = new DefinitionNameStartsWithCapitalLetter();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidDoesNotStartWithCapitalLetter() {
    ASTUnit astUnit = this.parseSysMLSingleModel(this.pathToInvalidModels
        + "/NamingConvention/Blocks Example.sysml");

    DefinitionNameStartsWithCapitalLetter coco = new DefinitionNameStartsWithCapitalLetter();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);

    assertEquals(1,Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.DefinitionNameStartsWithCapitalLetter))+" Name "
                + "\"vehicle\" should start "
                + "with a capital letter.",
            new SourcePosition(4, 7, "Blocks Example.sysml"))
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
