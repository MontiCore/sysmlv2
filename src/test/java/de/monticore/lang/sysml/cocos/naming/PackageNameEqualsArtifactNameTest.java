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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class PackageNameEqualsArtifactNameTest extends AbstractSysMLTest {
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
    List<ASTUnit> astUnits =
        this.validParseAndBuildSymbolsInSubDir("/packageEqualFile");

    PackageNameEqualsArtifactName coco = new PackageNameEqualsArtifactName();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    assertEquals(1, astUnits.size());
    coCoChecker.checkAll(astUnits.get(0));
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() {
    List<ASTUnit> astUnits = this.invalidParseAndBuildSymbolsInSubDir("/WrongPackageName");
    assertEquals(1, astUnits.size());

    PackageNameEqualsArtifactName coco = new PackageNameEqualsArtifactName();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnits.get(0));

    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());
    //this.printAllFindings();
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.PackageNameEqualsArtifactName)) +
                " package \"WrongName\" should be equal to the Filename.",
            new SourcePosition(1, 0, "Blocks Example.sysml"))
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
