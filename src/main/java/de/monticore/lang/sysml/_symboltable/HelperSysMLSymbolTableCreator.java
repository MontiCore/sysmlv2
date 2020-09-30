package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLLanguage;
import de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreator;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class HelperSysMLSymbolTableCreator {
 /* TODO delete public SysMLArtifactScope buildSymbolTable(ASTUnit astUnit, SysMLArtifactScope scope){
    SysMLSymbolTableCreator symbolTableCreator = new SysMLSymbolTableCreator(scope);
    SysMLArtifactScope newScope = symbolTableCreator.createFromAST(astUnit);
    return newScope;
  }
  public SysMLArtifactScope buildSymbolTableWithGlobalScope(ASTUnit astUnit,
      de.monticore.io.paths.ModelPath modelPath, SysMLLanguage language){
    SysMLGlobalScope globalScope = new SysMLGlobalScope(modelPath, language);
    SysMLSymbolTableCreator symbolTableCreator = new SysMLSymbolTableCreator(globalScope);
    SysMLArtifactScope newScope = symbolTableCreator.createFromAST(astUnit);
    return newScope;
  }*/

  public SysMLGlobalScope createSymboltable(ModelPath mp) {
    SysMLLanguageSub sysMLLanguage = new SysMLLanguageSub("SysML", ".sysml");
    SysMLGlobalScope sysMLGlobalScope = new SysMLGlobalScope(mp, sysMLLanguage);
    return sysMLGlobalScope;
  }
}
