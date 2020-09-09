package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.basics.sysmlcommon._symboltable.SysMLCommonSymbolTableCreator;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreator;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.monticore.lang.sysml.cocos.NamingConvention;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SymbolTableCreationTest extends AbstractSysMLTest {
  @Test
  public void testSuccessfulCreation() { //TODO
    ASTUnit astUnit =
        this.parseSysMLSingleModel(this.pathToOfficialSysMLExamples + "/02. Blocks/Blocks Example.sysml");
    // SysMLCommonSymbolTableCreator symbolTableCreator = new SysMLCommonSymbolTableCreator();
    //SysMLSymbolTableCreator symbolTableCreator = new SysMLSymbolTableCreator(astUnit);
  }
}
