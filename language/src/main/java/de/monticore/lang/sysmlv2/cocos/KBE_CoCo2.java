/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class KBE_CoCo2 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {

    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();

    String srcQName = endpointQName(src);
    String tgtQName = endpointQName(tgt);

    ISysMLPartsScope scope = node.getEnclosingScope();

    PortUsageSymbol srcPort = resolvePort(scope, srcQName);
    PortUsageSymbol tgtPort = resolvePort(scope, tgtQName);

    if (srcPort == null || tgtPort == null) {
      return;
    }

    boolean srcIsSub    = isSubcomponentEndpoint(srcQName);
    boolean srcIsParent = !srcIsSub;

    boolean srcHasInputPins = !srcPort.getInputAttributes().isEmpty();

    boolean tgtIsSub         = isSubcomponentEndpoint(tgtQName);
    boolean tgtHasOutputPins = !tgtPort.getOutputAttributes().isEmpty();

    // Verboten: Parent.Input -> Sub.Output
    if (srcIsParent && srcHasInputPins && tgtIsSub && tgtHasOutputPins) {
      Log.error(
          "0xKBE02 Illegal connection in ConnectionUsage '" + node.getName()
              + "': inputs of the parent component must not be connected to "
              + "output ports of subcomponents.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  protected String endpointQName(ASTEndpoint ep) {
    if (ep.getMCQualifiedName() != null) {
      return ep.getMCQualifiedName().toString();
    }
    return "";
  }

  protected boolean isSubcomponentEndpoint(String qname) {
    return qname.contains(".");
  }

  /** Port über einfachen Resolver auflösen (funktioniert garantiert). */
  protected PortUsageSymbol resolvePort(ISysMLPartsScope scope, String qname) {
    return scope.resolvePortUsage(qname).orElse(null);
  }
}
