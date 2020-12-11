package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTPackageUnitCoCo;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Robin Muenstermann
 * @version 1.0
 * This CoCos is a bit different than the normal MontiCore CoCos, because it compares the filename and the if the
 * hierarchical highest unit is a package unit with the same name as the filename.
 */
public class PackageNameEqualsFileName implements SysMLImportsAndPackagesASTPackageUnitCoCo {

  @Override
  public void check(ASTPackageUnit node) {
    if(node.getEnclosingScope() instanceof SysMLArtifactScope || node.getEnclosingScope() instanceof SysMLGlobalScope){
      //Only for the first package

    String name = node.getPackage().getPackageDeclaration().getSysMLName().getName();
    if(node.get_SourcePositionStart().getFileName().isPresent()){
      String relPath = node.get_SourcePositionStart().getFileName().get();
      Path path = Paths.get(relPath);
      String filename = path.getFileName().toString();
      if(filename.length()>6){ //If this is false, an warning will be printed anyway, so there is no need for else-block
        filename = filename.substring(0, filename.length()-6); // Remove .sysml
      }
      if(!name.equals(filename)){
        warnMessage(name, node);
      }
    }else {
      warnMessage(name, node);
    }
    }
  }

  public void warnMessage(String name, ASTPackageUnit node){
    Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.PackageNameEqualsArtifactName) + " package " +name+
        " should be equal to the Filename." , node.get_SourcePositionStart());
  }
}
