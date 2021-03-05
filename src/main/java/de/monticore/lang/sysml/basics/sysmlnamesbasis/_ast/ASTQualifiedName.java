package de.monticore.lang.sysml.basics.sysmlnamesbasis._ast;

import de.monticore.lang.sysml.basics.sysmlnamesbasis._symboltable.SysMLTypeSymbol;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public interface ASTQualifiedName extends ASTQualifiedNameTOP{
  public List<String> getNamesList();
  public String getFullQualifiedName();


  default public List<SysMLTypeSymbol> resolveSymbols(){
    return de.monticore.lang.sysml.basics.sysmlnamesbasis._ast.ResolveQualifiedNameHelper.resolveSymbolsHelper(this);
  }

}
