package de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ASTColonQualifiedName extends ASTColonQualifiedNameTOP {
  @Override
  public List<String> getNamesList(){
    List<String> names = new ArrayList<>();
    for (ASTSysMLName name : this.getSysMLNameList()) {
      names.add(name.getName());
    }
    return names;
  }

  @Override
  public String getFullQualifiedName() {
    String res = "";
    for (String name : this.getNamesList()) {
      res += "." + name;
    }
    return res.substring(1);
  }

  @Override
  public List<SysMLTypeSymbol> resolveSymbols() {
    return ASTQualifiedName.resolveSymbolsHelper(this);
  }
}
