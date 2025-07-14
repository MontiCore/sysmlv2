/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.language_access;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2GlobalScope;
import de.se_rwth.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SysMLv2ScopeManager extends SysMLv2ScopeManagerTOP {


  private static SysMLv2ScopeManager instance;

  public SysMLv2ScopeManager() {
    super();
    instance = this;
  }

  public static SysMLv2ScopeManager getInstance() {
    return instance;
  }

  private static final Logger logger = LoggerFactory.getLogger(SysMLv2ScopeManager.class);

  @Override
  public void initGlobalScope(MCPath modelPath) {
    // Mill init, clear global scope, set internal reference to this global scope
    super.initGlobalScope(modelPath);

    // Load globally available symbols like int, boolean, List
    SysMLv2Mill.prepareGlobalScope();
  }

  @Override
  public boolean supportsIterativeScopeAppending() {
    return true;
  }

  @Override
  public SysMLv2ArtifactScopeWithFindings createArtifactScope(
      ASTSysMLModel ast,
      ISysMLv2ArtifactScope oldArtifactScope
  ) {

    return createArtifactScope(ast, oldArtifactScope, true);
  }

  public SysMLv2ArtifactScopeWithFindings createArtifactScope(
      ASTSysMLModel ast,
      ISysMLv2ArtifactScope oldArtifactScope,
      boolean createSymbolTable
  ) {

    var scope = super.createArtifactScope(ast, oldArtifactScope);
    if (createSymbolTable){
      syncAccessGlobalScope(gs -> {
        var tool = new SysMLv2Tool();
        tool.completeSymbolTable(ast);
        tool.finalizeSymbolTable(ast);
      });
    }
    return scope;
  }

  @Override
  public Map<ASTSysMLModel, SysMLv2ArtifactScopeWithFindings> createAllArtifactScopes(Collection<ASTSysMLModel> astNodes){
    final Map<ASTSysMLModel, SysMLv2ArtifactScopeWithFindings> res = new HashMap<>();
    syncAccessGlobalScope(gs -> {
      clearGlobalScope();
      if(supportsIterativeScopeAppending()){
        for(ASTSysMLModel node : astNodes){
          Log.getFindings().clear();
          res.put(node, createArtifactScope(node, null, false));
        }
      } else {
        // TODO: print warning only once
        logger.warn("createAllArtifactScopes is not implemented. Please provide an implementation via the TOP-mechanism on SysMLv2ScopeManagerTOP");
      }

      SysMLv2Tool tool = new SysMLv2Tool();
      for(ASTSysMLModel node : astNodes){
        tool.completeSymbolTable(node);
      }
      for(ASTSysMLModel node : astNodes){
        tool.finalizeSymbolTable(node);
      }
    });
    return res;
  }
}
