package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ScopeCreationTest extends AbstractSysMLTest {

  @Ignore
  @Test
  public void scopeNames(){
    String modelPath = this.pathToValidModels + "/naming/";
    ASTUnit astUnit =
        this.parseSysMLSingleModel(modelPath);
    String currentPath = this.pathToOfficialSysMLTrainingExamples;
    List<ASTUnit> models = SysMLTool.parseDirectory(currentPath);
    SysMLGlobalScope topScope = SysMLTool.buildSymbolTable(currentPath, models);




  }
}
