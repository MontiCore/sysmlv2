package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.*;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class HelperSysMLSymbolTableCreator {

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

    AddImportToScopeVisitor addImportToScopeVisitor = new AddImportToScopeVisitor();
    for(ASTUnit model: astUnits){
      addImportToScopeVisitor.memorizeImportsPhase1of2(model);
    }
    for(ASTUnit model: astUnits){
      addImportToScopeVisitor.addImportsToScopePhase2of2(model);
    }

    return globalScope;
  }
}
