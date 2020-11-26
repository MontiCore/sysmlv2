package de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public interface ASTQualifiedName extends ASTQualifiedNameTOP{
  public List<String> getNamesList();
  public String getFullQualifiedName();


  default public List<SysMLTypeSymbol> resolveSymbols(){
    return ResolveQualifiedNameHelper.resolveSymbolsHelper(this);
  }


}
