package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml.SysMLMill;
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

  public ISysMLGlobalScope initGlobalScope(ModelPath mp) {
    ISysMLGlobalScope sysMLGlobalScope = SysMLMill.sysMLGlobalScopeBuilder().setModelPath(mp).setModelFileExtension("sysml").build();
    return sysMLGlobalScope;
  }

  public ISysMLArtifactScope createSymboltable(ASTUnit ast, ISysMLGlobalScope globalScope) {

    SysMLSymbolTableCreatorDelegator symbolTableDelegator = SysMLMill.sysMLSymbolTableCreatorDelegatorBuilder().setGlobalScope(globalScope).build();
    return symbolTableDelegator.createFromAST(ast);
  }

  public ISysMLArtifactScope createSymboltableSingleASTUnit(ASTUnit astUnit, ModelPath mp){
    return createSymboltable(astUnit, initGlobalScope(mp));
  }
  public ISysMLGlobalScope createSymboltableMultipleASTUnit(List<ASTUnit> astUnits, ModelPath mp){ //TODO test
    ISysMLGlobalScope globalScope = initGlobalScope(mp);
    for (ASTUnit astUnit : astUnits) {
      createSymboltable(astUnit,globalScope );
    }
    return globalScope;
  }
}
