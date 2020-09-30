package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.*;

import java.util.List;

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

  public SysMLLanguageSub initSysMLLang() {
    SysMLLanguageSub sysMLLanguage = new SysMLLanguageSub("SysML", ".sysml");
    return sysMLLanguage;
  }

  public SysMLGlobalScope initGlobalScope(ModelPath mp, SysMLLanguageSub sysMLLanguage) {
    SysMLGlobalScope sysMLGlobalScope = new SysMLGlobalScope(mp, sysMLLanguage);
    return sysMLGlobalScope;
  }

  public SysMLArtifactScope createSymboltable(ASTUnit ast,SysMLLanguageSub sysMLLanguage, SysMLGlobalScope globalScope) {

    SysMLSymbolTableCreatorDelegator symbolTableDelegator = sysMLLanguage.getSymbolTableCreator(globalScope);
    return symbolTableDelegator.createFromAST(ast);
  }

  public SysMLArtifactScope createSymboltableSingleASTUnit(ASTUnit astUnit, ModelPath mp){
    return createSymboltable(astUnit, initSysMLLang(), initGlobalScope(mp, initSysMLLang()));
  }
  public SysMLGlobalScope createSymboltableMultipleASTUnit(List<ASTUnit> astUnits, ModelPath mp){ //TODO test
    SysMLLanguageSub lang = initSysMLLang();
    SysMLGlobalScope globalScope = initGlobalScope(mp, lang);
    for (ASTUnit astUnit : astUnits) {
      createSymboltable(astUnit, lang,globalScope );
    }
    return globalScope;
  }
}
