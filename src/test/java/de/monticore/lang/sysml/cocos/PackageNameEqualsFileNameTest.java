package de.monticore.lang.sysml.cocos;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class PackageNameEqualsFileNameTest extends AbstractSysMLTest {
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
        this.parseSysMLSingleModel(this.pathToOfficialSysMLExamples + "/02. Blocks/Blocks Example.sysml");

    PackageNameEqualsFileName coco = new PackageNameEqualsFileName();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidDoesNotStartWithCapitalLetter() {
    ASTUnit astUnit = this.parseSysMLSingleModel(this.pathToInvalidModels
        + "/WrongPackageName/Blocks Example.sysml");

    PackageNameEqualsFileName coco = new PackageNameEqualsFileName();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);

    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());
    //this.printAllFindings();
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning("0xSysML03 package 'WrongName' should be equal to the Filename.",
            new SourcePosition(1, 0, "Blocks Example.sysml"))
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
