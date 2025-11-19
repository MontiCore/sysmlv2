/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

public class KBE_CoCo6 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // Wir brauchen beide Endpunkte
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    EndpointInfo src = parseEndpoint(node.getSrc());
    EndpointInfo tgt = parseEndpoint(node.getTgt());

    // Fall 1: src = Parent, tgt = Subkomponente
    if (!src.isSub && tgt.isSub && isInputName(tgt.portName)) {
      checkNaming(node, src, tgt);
    }
    // Fall 2: src = Subkomponente, tgt = Parent
    else if (src.isSub && !tgt.isSub && isInputName(src.portName)) {
      checkNaming(node, tgt, src);
    }
    // alle anderen Kombinationen sind für CoCo 6 irrelevant
  }

  /**
   * Prüft, ob der Portname des Oberkomponenten-Ports den Namen der Subkomponente enthält.
   * parent = Endpunkt im Oberkomponenten-Kontext (kein '.'),
   * sub    = Endpunkt im Subkomponenten-Kontext (mit '.').
   */
  protected void checkNaming(ASTConnectionUsage conn,
                             EndpointInfo parent,
                             EndpointInfo sub) {

    String parentNameLower = parent.portName.toLowerCase();
    String subCompLower    = sub.subcomponentName.toLowerCase();

    if (!parentNameLower.contains(subCompLower)) {
      // einfacher Vorschlag: subKomponentenName + "_" + alterPortName
      String suggestion = sub.subcomponentName + "_" + parent.portName;

      Log.error(
          "0xKBE06 Naming inconsistency in ConnectionUsage '" + conn.getName()
              + "': parent port '" + parent.portName
              + "' is directly connected to input port '" + sub.qname
              + "', but does not contain the subcomponent name '" + sub.subcomponentName
              + "'. Suggested name: '" + suggestion + "'.",
          conn.get_SourcePositionStart(),
          conn.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Kleine Hilfsklasse für Informationen über einen Endpunkt.
   */
  protected static class EndpointInfo {
    final String qname;             // kompletter Qualified Name
    final boolean isSub;            // true, wenn "sub.port"
    final String subcomponentName;  // Name vor dem '.', oder "" wenn isSub == false
    final String portName;          // Name nach dem letzten '.', oder kompletter Name wenn kein '.'

    EndpointInfo(String qname, boolean isSub, String subcomponentName, String portName) {
      this.qname = qname;
      this.isSub = isSub;
      this.subcomponentName = subcomponentName;
      this.portName = portName;
    }
  }

  /**
   * Parsed einen ASTEndpoint in eine EndpointInfo-Struktur.
   */
  protected EndpointInfo parseEndpoint(ASTEndpoint ep) {
    String qname = "";
    if (ep.getMCQualifiedName() != null) {
      qname = ep.getMCQualifiedName().toString();
    }

    String subName = "";
    String portName = qname;

    int dotIdx = qname.indexOf('.');
    boolean isSub = dotIdx >= 0;

    if (isSub) {
      subName = qname.substring(0, dotIdx);
      int lastDot = qname.lastIndexOf('.');
      portName = qname.substring(lastDot + 1);
    }

    return new EndpointInfo(qname, isSub, subName, portName);
  }

  /**
   * Heuristik: typische Input-Namen enden auf "In" oder "input".
   */
  protected boolean isInputName(String name) {
    String lower = name.toLowerCase();
    return lower.endsWith("in") || lower.endsWith("input");
  }
}
