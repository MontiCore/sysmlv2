/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class KBE_CoCo6 implements SysMLPartsASTConnectionUsageCoCo {

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

    // Fall 1: Parent (src) -> Sub(Input) (tgt)
    if (!srcIsSub && tgtIsSub && !tgtPort.getInputAttributes().isEmpty()) {
      checkParentNameForSubInput(node, srcQName, tgtQName);
    }

    // Fall 2: Sub(Input) (src) -> Parent (tgt)
    if (srcIsSub && !tgtIsSub && !srcPort.getInputAttributes().isEmpty()) {
      checkParentNameForSubInput(node, tgtQName, srcQName);
    }
  }

  /**
   * Prüft: Port des Oberkomponenten (parentQName) ist mit einem Input-Port
   * einer Subkomponente (subQName) verbunden. Dann soll der Parent-Portname
   * den Subkomponentennamen enthalten.
   */
  protected void checkParentNameForSubInput(ASTConnectionUsage conn,
                                            String parentQName,
                                            String subQName) {

    String parentPortName = parentQName; // Parent hat keinen Punkt im Namen
    String subCompName    = subQName.split("\\.")[0];

    String ppLower = parentPortName.toLowerCase();
    String subLower = subCompName.toLowerCase();

    if (!ppLower.contains(subLower)) {
      String suggestion = subCompName + capitalize(parentPortName);

      Log.error(
          "0xKBE06 Naming inconsistency in ConnectionUsage '" + conn.getName()
              + "': port '" + parentPortName + "' of the parent component is "
              + "connected to an input port of subcomponent '" + subCompName
              + "', but does not contain the subcomponent name. "
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

  /** Subkomponenten-Port, wenn ein Punkt im Namen vorkommt (z.B. "tele.maniacIn"). */
  protected boolean isSubcomponentEndpoint(String qname) {
    return qname.contains(".");
  }

  /** Port über einfachen Resolver auflösen. */
  protected PortUsageSymbol resolvePort(ISysMLPartsScope scope, String qname) {
    return scope.resolvePortUsage(qname).orElse(null);
  }

  protected String capitalize(String s) {
    if (s == null || s.isEmpty()) return s;
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
