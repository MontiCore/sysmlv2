package de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ASTSimpleName extends ASTSimpleNameTOP {
  @Override
  public List<String> getNamesList(){
    List<String> names = new ArrayList<>();
    names.add(this.getReferencedName());
    return names;
  }
  @Override
  public String getFullQualifiedName() {
    String res = "";
    for (String name : this.getNamesList()) {
      res += name;
    }
    return res;
  }

  @Override
  public List<SysMLTypeSymbol> resolveSymbols() {
    return ASTQualifiedName.resolveSymbolsHelper(this);
  }

  public String getReferencedName(){
    return  this.getSysMLName().getName();
  }
}
