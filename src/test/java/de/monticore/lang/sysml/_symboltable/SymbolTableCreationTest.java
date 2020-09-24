package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import org.junit.Test;

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
