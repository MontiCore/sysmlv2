package de.monticore.lang.sysmlactions._ast;

import java.util.List;

import static java.util.Collections.emptyList;

public class ASTCalcDef extends ASTCalcDefTOP {

  /**
   * Diese Methode ... (warum gibt es diese Methode, was liefert die Methode)
   */
  public List<String> getInputParameters() {
    // TODO Kann man implementieren, zB (Pseudo-Code)
    /*
    getElements().stream()
      .filter(it.modifier.anyMatch(it.isInput()))
      .filter(it isinstanceOf AttributeUsage || it instanceof AnonymousUsage)
      .collect(toList)
     */
    return emptyList();
  }

}
