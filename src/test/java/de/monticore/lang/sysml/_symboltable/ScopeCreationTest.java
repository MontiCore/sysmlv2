package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ScopeCreationTest extends AbstractSysMLTest {

  @Test
  @Ignore // TODO fix me
  public void testScopeNames() {
    String modelPath = this.pathToValidModels + "/naming/";
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    ISysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    // System.out.println("Top scope name: " + topScope.getName());

    List<ISysMLScope> scopes = this.printSubScopesName(topScope, "  ");
    assertEquals("Car",scopes.get(2).getName());
    assertEquals("SimpleBlocksExample",scopes.get(1).getName());
  }

  private List<ISysMLScope> printSubScopesName(ISysMLScope scope, String intendation) {
    List<ISysMLScope> scopes = new ArrayList<>();
    if(scope instanceof SysMLArtifactScope){
      Log.debug(intendation + "This is an Artifact Scope.", this.getClass().getName());
    }
    if(scope instanceof SysMLGlobalScope){
      Log.debug(intendation + "This is a Global Scope.", this.getClass().getName());
    }
    if(scope.getSubScopes().size()!= 0) {
      Log.debug(intendation + "Found " + scope.getSubScopes().size() + " subscopes. Printing the names now:",
          this.getClass().getName());
      for (ISysMLScope subscope : scope.getSubScopes()) {
        scopes.add(subscope);
        if (subscope.isPresentName()) {
          Log.debug(intendation + "-" + subscope.getName(),this.getClass().getName());
          scopes.addAll(this.printSubScopesName(subscope, intendation + "  "));
        }
      }
    }
    return scopes;
  }
}
