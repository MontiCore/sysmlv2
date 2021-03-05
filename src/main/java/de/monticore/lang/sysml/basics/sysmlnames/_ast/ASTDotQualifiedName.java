package de.monticore.lang.sysml.basics.sysmlnames._ast;

import de.monticore.lang.sysml.basics.sysmlnamesbasis._ast.ASTSysMLName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ASTDotQualifiedName extends ASTDotQualifiedNameTOP {
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

  public String getReferencedName(){
    return  this.getSysMLName(this.getSysMLNameList().size()-1).getName();
  }
}
