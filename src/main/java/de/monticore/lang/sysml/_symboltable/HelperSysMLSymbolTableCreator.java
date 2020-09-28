package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreator;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class HelperSysMLSymbolTableCreator {
  public SysMLArtifactScope buildSymbolTable(ASTUnit astUnit, SysMLArtifactScope scope){
    SysMLSymbolTableCreator symbolTableCreator = new SysMLSymbolTableCreator(scope);
    SysMLArtifactScope newScope = symbolTableCreator.createFromAST(astUnit);
    return newScope;
  }
}
