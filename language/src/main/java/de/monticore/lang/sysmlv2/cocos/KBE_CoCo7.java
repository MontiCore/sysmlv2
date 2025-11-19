/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

public class KBE_CoCo7 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    //  Ein Port eines Subkomponents, der direkt mit einem Output-Port des
    //  Oberkomponents verbunden ist, sollte einen Namen tragen, der den Namen
    //  dieser Subkomponente enthält.

    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    EndpointInfo src = parseEndpoint(node.getSrc());
    EndpointInfo tgt = parseEndpoint(node.getTgt());

    // src = Subkomponente, tgt = Parent-Output
    if (src.isSub && !tgt.isSub && isOutputName(tgt.portName)) {
      checkSubPortNaming(node, src, tgt);
    }
    
  }

  /**
   * Prüft: Der Portname der Subkomponente soll deren Namen enthalten.
   *
   * @param conn   die ConnectionUsage
   * @param sub    Endpunkt im Subkomponenten-Kontext ("sub.port")
   * @param parent Endpunkt im Oberkomponenten-Kontext (kein '.')
   */
  protected void checkSubPortNaming(ASTConnectionUsage conn,
                                    EndpointInfo sub,
                                    EndpointInfo parent) {

    if (sub.subcomponentName.isEmpty()) {
      return;
    }

    String subPortLower = sub.portName.toLowerCase();
    String subCompLower = sub.subcomponentName.toLowerCase();

    if (!subPortLower.contains(subCompLower)) {
      // Vorschlag: subKomponentenName + "_" + alterPortName
      String suggestion = sub.subcomponentName + "_" + sub.portName;

      Log.error(
          "0xKBE07 Naming inconsistency in ConnectionUsage '" + conn.getName()
              + "': subcomponent port '" + sub.qname
              + "' is directly connected to output port '" + parent.qname
              + "' of the parent component, but its name does not contain the "
              + "subcomponent name '" + sub.subcomponentName + "'. "
              + "Suggested name: '" + suggestion + "'.",
          conn.get_SourcePositionStart(),
          conn.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Kleine Hilfsklasse
   */
  protected static class EndpointInfo {
    final String qname;             // kompletter Qualified Name, z.B. "tele.maniacOut"
    final boolean isSub;            // true, wenn "sub.port" (also mit '.')
    final String subcomponentName;  // Name vor dem ersten '.', oder "" wenn isSub == false
    final String portName;          // Name nach dem letzten '.', oder kompletter Name wenn kein '.'

    EndpointInfo(String qname, boolean isSub, String subcomponentName, String portName) {
      this.qname = qname;
      this.isSub = isSub;
      this.subcomponentName = subcomponentName;
      this.portName = portName;
    }
  }

  /**
   * Parsed einen ASTEndpoint in eine EndpointInfo-Struktur:
   * - "tele.maniacOut" -> subcomponentName="tele", portName="maniacOut"
   * - "groundOut"      -> subcomponentName="",    portName="groundOut"
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
   * Heuristik: typische Output-Namen enden auf "Out" oder "output".
   * (genau wie bei den anderen CoCos, damit es konsistent bleibt)
   */
  protected boolean isOutputName(String name) {
    String lower = name.toLowerCase();
    return lower.endsWith("out") || lower.endsWith("output");
  }
}
