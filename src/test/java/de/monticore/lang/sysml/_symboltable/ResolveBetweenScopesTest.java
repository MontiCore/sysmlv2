package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ResolveBetweenScopesTest extends AbstractSysMLTest {
  @Ignore
  @Test
  public void resolveToOtherScope(){

    String modelPath = this.pathToValidModels + "/scopes/simple";
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    SysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    Optional<ASTPackage> packageWithImport = Optional.empty();
    for(ASTUnit model : models){
      if(model instanceof ASTPackage){
        if(((ASTPackage) model).getName().equals("Import Vehicle")){
          packageWithImport = Optional.of((ASTPackage) model);
        }
      }
    }
    assertTrue(packageWithImport.isPresent());
    packageWithImport.get().getEnclosingScope().resolveSysMLType("Vehicle");
  }
}
