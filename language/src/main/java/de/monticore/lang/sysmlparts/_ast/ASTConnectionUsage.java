/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;

import java.util.ArrayList;
import java.util.List;

public class ASTConnectionUsage extends ASTConnectionUsageTOP {

  /**
   * @return All ends of this connection: source and target of the binary form, or the ends of the
   * n-ary form.
   */
  public List<ASTEndpoint> getEnds() {
    List<ASTEndpoint> ends = new ArrayList<>();
    if (isPresentSrc()) {
      ends.add(getSrc());
    }
    if (isPresentTgt()) {
      ends.add(getTgt());
    }
    ends.addAll(getEndpointList());
    return ends;
  }
}
