package de.monticore.lang.sysmlvariant.basics.objectoriented.sysmlnamesoo._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ASTDotQualifiedNameOO extends ASTDotQualifiedNameOOTOP {

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
      res += "::" + name;
    }
    return res.substring(2);
  }

  @Override
  public List<SysMLTypeSymbol> resolveSymbols() {
    return ASTQualifiedName.resolveSymbolsHelper(this);
  }

  public String getReferencedName(){
    return  this.getSysMLName(this.getSysMLNameList().size()-1).getName();
  }
}
