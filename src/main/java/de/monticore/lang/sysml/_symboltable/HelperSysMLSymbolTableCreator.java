package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.ScopeNameVisitor;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreatorDelegator;

import java.util.ArrayList;
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

    /*SysMLSymbolTableCreatorDelegator symbolTableDelegator = sysMLLanguage.getSymbolTableCreator(globalScope);
    SysMLArtifactScope artifactScope =  symbolTableDelegator.createFromAST(ast);
    ScopeNameVisitor scopeNameVisitor = new ScopeNameVisitor(); TODO
    scopeNameVisitor.handle(ast);*/
    SysMLSymbolTableCreatorDelegator symbolTableDelegator = sysMLLanguage.getSymbolTableCreator(globalScope);
    SysMLArtifactScope artifactScope =  symbolTableDelegator.createFromAST(ast);
    ScopeNameVisitor scopeNameVisitor = new ScopeNameVisitor();
    scopeNameVisitor.startTraversal(ast);
    return artifactScope;
  }

  public SysMLArtifactScope createSymboltableSingleASTUnit(ASTUnit astUnit, ModelPath mp){
    return createSymboltable(astUnit, initSysMLLang(), initGlobalScope(mp, initSysMLLang()));
  }
  public SysMLGlobalScope createSymboltableMultipleASTUnit(List<ASTUnit> astUnits, ModelPath mp){
    SysMLLanguageSub lang = initSysMLLang();
    SysMLGlobalScope globalScope = initGlobalScope(mp, lang);
    for (ASTUnit astUnit : astUnits) {
      createSymboltable(astUnit, lang,globalScope );
    }
    return globalScope;
  }
}
