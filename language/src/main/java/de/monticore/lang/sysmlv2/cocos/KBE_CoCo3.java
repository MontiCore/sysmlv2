/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;

public class KBE_CoCo3 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {

    // Wir brauchen sowohl src als auch tgt, sonst macht die Prüfung keinen Sinn
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      Log.error(
          "0xKBE030 ConnectionUsage '" + node.getName()
              + "' must have both a source and a target endpoint.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
      return;
    }

    ISysMLPartsScope scope = node.getEnclosingScope();

    // Quelle und Ziel getrennt prüfen
    checkEndpoint(node, node.getSrc(), scope, true);
    checkEndpoint(node, node.getTgt(), scope, false);
  }

  /**
   * Prüft: Der im Endpunkt verwendete Portname muss im Modell existieren und eindeutig sein.
   */
  protected void checkEndpoint(ASTConnectionUsage conn,
                               ASTEndpoint ep,
                               ISysMLPartsScope scope,
                               boolean isSource) {

    String qname = endpointQName(ep);
    if (qname.isEmpty()) {
      return;
    }

    String portName = lastSegment(qname);
    String side = isSource ? "source" : "target";

    // Alle PortUsage-Symbole holen und nach Namen filtern
    Collection<PortUsageSymbol> allPorts =
        scope.getPortUsageSymbols().get(portName);

    int count = allPorts.size();

    if (count == 0) {
      Log.error(
          "0xKBE031 The " + side + " endpoint '" + qname
              + "' in connection '" + conn.getName()
              + "' refers to port '" + portName
              + "', which does not exist in the model.",
          conn.get_SourcePositionStart(),
          conn.get_SourcePositionEnd()
      );
    }
    else if (count > 1) {
      Log.error(
          "0xKBE032 The " + side + " endpoint '" + qname
              + "' in connection '" + conn.getName()
              + "' refers to port '" + portName
              + "', which is ambiguous (" + count
              + " matching ports). Each connected port name must be unique.",
          conn.get_SourcePositionStart(),
          conn.get_SourcePositionEnd()
      );
    }
    // count == 1 → alles OK
  }

  /** Liefert den qualifizierten Namen des Endpunkts als String. */
  protected String endpointQName(ASTEndpoint ep) {
    if (ep.getMCQualifiedName() != null) {
      return ep.getMCQualifiedName().toString();
    }
    return "";
  }

  /** Gibt das letzte Segment eines durch '.' getrennten Namens zurück. */
  protected String lastSegment(String qname) {
    int idx = qname.lastIndexOf('.');
    if (idx < 0) {
      return qname;
    }
    return qname.substring(idx + 1);
  }
}
