package de.monticore.lang.sysml.cocos.imports;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ResolveQualifiedNameHelper;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTImportUnitStdCoCo;
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
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AmbiguousImportTest extends AbstractSysMLTest {
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Test
  @Ignore // TODO fix me
  public void testValid(){
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/simple");
    ImportStatementValid coco = new ImportStatementValid();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo) coco);
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTImportUnitStdCoCo) coco);
    for (ASTUnit model : models) {
      coCoChecker.checkAll(model);
    }
    printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  @Ignore // TODO fix me
  public void testInvalid(){
    List<ASTUnit> models = this.invalidParseAndBuildSymbolsInSubDir("/imports/AmbiguousImport");
    List<String> blockAQN = new ArrayList();
    blockAQN.add("Symbol Definition");
    blockAQN.add("Vehicle");
    List<SysMLTypeSymbol> blockA =
        ResolveQualifiedNameHelper.resolveQualifiedNameAsListInASpecificScope(blockAQN,
            models.get(0).getEnclosingScope().getEnclosingScope());
    assertEquals(2, blockA.size());

    ImportStatementValid coco = new ImportStatementValid();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo) coco);
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTImportUnitStdCoCo) coco);
    for (ASTUnit model : models) {
      coCoChecker.checkAll(model);
    }
    //this.printAllFindings();
    assertEquals(1, Log.getFindings().size());

    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());

    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.NoAmbiguousImport)) +
                " The import statement was ambiguous, nothing will be imported.",
            new SourcePosition(2, 2, "Import Vehicle.sysml"))
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}