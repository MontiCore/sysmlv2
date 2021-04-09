package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml.SysMLMill;
import de.monticore.lang.sysml.sysml._symboltable.*;
import de.monticore.lang.sysml.sysml._symboltable.doubleimports.AmbigousImportCheck;
import de.monticore.lang.sysml.sysml._symboltable.doubleimports.RemoveDoubleImportsFromScope;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverserImplementation;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class HelperSysMLSymbolTableCreator {

  public ISysMLGlobalScope initGlobalScope(ModelPath mp) {
    ISysMLGlobalScope sysMLGlobalScope = SysMLMill.globalScope();
    sysMLGlobalScope.clear();
    sysMLGlobalScope.setModelPath(mp);
    return sysMLGlobalScope;
  }

  public ISysMLArtifactScope createSymboltable(ASTUnit ast, ISysMLGlobalScope globalScope) {

    SysMLMill.init();
    SysMLScopesGenitorDelegator symbolTableDelegator = SysMLMill.scopesGenitorDelegator();
        return symbolTableDelegator.createFromAST(ast);
  }

  public ISysMLArtifactScope createSymboltableSingleASTUnit(ASTUnit astUnit, ModelPath mp){
    return createSymboltable(astUnit, initGlobalScope(mp));
  }

  public ISysMLGlobalScope createSymboltableMultipleASTUnit(List<ASTUnit> astUnits, ModelPath mp){ //TODO test
    ISysMLGlobalScope globalScope= initGlobalScope(mp);
    for (ASTUnit astUnit : astUnits) {
      createSymboltable(astUnit,globalScope );
    }

    AddVisibilityToSymbolVisitor addVisibilityToSymbolVisitor = new AddVisibilityToSymbolVisitor();
    for (ASTUnit astUnit : astUnits) {
      addVisibilityToSymbolVisitor.startTraversal(astUnit);
    }

    AddImportToScopeVisitor addImportToScopeVisitor = new AddImportToScopeVisitor();
    for(ASTUnit model: astUnits){
      addImportToScopeVisitor.memorizeImportsPhase1of5(model);
    }

    AmbigousImportCheck ambigousImportCheck = new AmbigousImportCheck();
    for(ASTUnit model: astUnits){
      ambigousImportCheck.addWarningForAmbigousImport2of5(model);
    }

    for(ASTUnit model: astUnits){
      addImportToScopeVisitor.addReexportedSymbolsOfPackagesPhase3of5(model);
    }

    RemoveDoubleImportsFromScope removeDoubleImportsFromScope = new RemoveDoubleImportsFromScope();
    for (ASTUnit model : astUnits) {
      removeDoubleImportsFromScope.removeDoubleImportsAndAddWarningPhase4of5(model);
    }

    for(ASTUnit model: astUnits){
      addImportToScopeVisitor.addImportsToScopePhase5of5(model);
    }

    return globalScope;
  }
}
