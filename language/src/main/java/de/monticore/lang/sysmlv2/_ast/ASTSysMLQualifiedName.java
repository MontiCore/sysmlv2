package de.monticore.lang.sysmlv2._ast;

import de.se_rwth.commons.Names;

public class ASTSysMLQualifiedName extends ASTSysMLQualifiedNameTOP {

  public ASTSysMLQualifiedName() {
    super();
  }

  public Boolean isQualified() {
    return getPartsList().size() >=2 ;
  }

  public String getBaseName() {
    return getPartsList().get(getPartsList().size()-1);
  }

  public String getQName() {
    return Names.constructQualifiedName(
        this.getPartsList());
  }

  public String toString() {
    return getQName();
  }



}
