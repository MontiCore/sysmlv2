/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

public class KBE_CoCo7 implements SysMLPartsASTConnectionUsageCoCo {

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

    boolean srcIsSub = isSubcomponentEndpoint(srcQName);
    boolean tgtIsSub = isSubcomponentEndpoint(tgtQName);

    // Fall 1: Sub-Port (src) <-> Parent-Output (tgt)
    if (srcIsSub && !tgtIsSub && !tgtPort.getOutputAttributes().isEmpty()) {
      checkSubPortNameForParentOutput(node, srcQName);
    }

    // Fall 2: Parent-Output (src) <-> Sub-Port (tgt)
    if (!srcIsSub && tgtIsSub && !srcPort.getOutputAttributes().isEmpty()) {
      checkSubPortNameForParentOutput(node, tgtQName);
    }
  }

  /**
   * Prüft: Port eines Subkomponenten (subQName) ist mit einem Output-Port
   * der Oberkomponente verbunden. Dann soll der (lokale) Portname der
   * Subkomponente den Subkomponentennamen enthalten.
   */
  protected void checkSubPortNameForParentOutput(ASTConnectionUsage conn,
                                                 String subQName) {

    String[] parts = subQName.split("\\.");
    if (parts.length < 2) {
      return; // zur Sicherheit – sollte bei Subkomponenten aber immer mind. 2 sein
    }

    String subCompName = parts[0];
    String portName    = parts[parts.length - 1];

    String subLower  = subCompName.toLowerCase();
    String portLower = portName.toLowerCase();

    if (!portLower.contains(subLower)) {
      String suggestion = subCompName + capitalize(portName);

      Log.error(
          "0xKBE07 Naming inconsistency in ConnectionUsage '" + conn.getName()
              + "': port '" + portName + "' of subcomponent '" + subCompName
              + "' is connected to an output port of the parent component, "
              + "but does not contain the subcomponent name. "
              + "For example, you could rename it to '" + suggestion + "'.",
          conn.get_SourcePositionStart(),
          conn.get_SourcePositionEnd()
      );
    }
  }

  // -------- Hilfsmethoden --------

  protected String endpointQName(ASTEndpoint ep) {
    if (ep.getMCQualifiedName() != null) {
      return ep.getMCQualifiedName().toString();
    }
    return "";
  }

  protected boolean isSubcomponentEndpoint(String qname) {
    return qname.contains(".");
  }

  protected PortUsageSymbol resolvePort(ISysMLPartsScope scope, String qname) {
    return scope.resolvePortUsage(qname).orElse(null);
  }

  protected String capitalize(String s) {
    if (s == null || s.isEmpty()) return s;
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
