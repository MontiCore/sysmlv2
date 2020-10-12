package de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public interface ASTQualifiedName extends ASTQualifiedNameTOP{
  public List<String> getNamesList();
  public String getFullQualifiedName();
}
