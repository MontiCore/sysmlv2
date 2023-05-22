package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ASTPartDef extends ASTPartDefTOP {

  /***
   * @param type The type of SysML elements to search for.
   * @return List of SysML elements of the given type.
   */
  public <T extends ASTSysMLElement> List<T> getSysMLElements(Class<T> type){
    return this.isEmptySysMLElements() ?
        new ArrayList<>() : this.getSysMLElementList().stream()
        .filter(type::isInstance)
        .map(e -> (T) e)
        .collect(Collectors.toList());
  }
}
