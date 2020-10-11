package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLScope;
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
    String currentPath = this.pathToOfficialSysMLTrainingExamples;
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    SysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    // System.out.println("Top scope name: " + topScope.getName());
    System.out.println("Found " + topScope.getSubScopes().size() + " subscopes. Printing the names now:");
    this.printSubScopesName(topScope, " ");



  }

  private void printSubScopesName(ISysMLScope scope, String intendation){
    for( ISysMLScope subscope: scope.getSubScopes()){
      System.out.println("-" + subscope.getName());
      this.printSubScopesName(subscope, intendation + " ");
    }
  }
}
