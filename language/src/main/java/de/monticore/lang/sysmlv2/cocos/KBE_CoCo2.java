/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.se_rwth.commons.logging.Log;

public class KBE_CoCo2 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // CoCo 2:
    // "Inputs von Oberkomponenten können nicht zu Outputs von Subkomponenten verbunden werden."

    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      // Ohne vollständige Verbindung macht die Regel keinen Sinn
      return;
    }

    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();

    String srcName = endpointName(src);
    String tgtName = endpointName(tgt);

    // Quelle klassifizieren
    boolean srcIsSub    = isSubcomponentEndpoint(srcName);
    boolean srcIsParent = !srcIsSub;
    boolean srcIsInput  = isInputName(srcName);

    // Ziel klassifizieren
    boolean tgtIsSub    = isSubcomponentEndpoint(tgtName);
    boolean tgtIsOutput = isOutputName(tgtName);

    // Verbotene Kombination:
    // Oberkomponenten-Input -> Subkomponenten-Output
    boolean forbidden = srcIsParent && srcIsInput && tgtIsSub && tgtIsOutput;

    if (forbidden) {
      Log.error(
          "0xKBE02 Illegal connection in ConnectionUsage '" + node.getName()
              + "': inputs of the parent component must not be connected to "
              + "outputs of subcomponents.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  /** Liefert den qualifizierten Namen des Endpunkts als String. */
  protected String endpointName(ASTEndpoint ep) {
    if (ep.getMCQualifiedName() != null) {
      return ep.getMCQualifiedName().toString();
    }
    return "";
  }

  /** Heuristik: ein Name mit '.' wird als Subkomponenten-Port interpretiert (z.B. "tele.maniacOut"). */
  protected boolean isSubcomponentEndpoint(String qname) {
    return qname.contains(".");
  }

  /** Heuristik: typische Output-Namen enden auf "Out" oder "output". */
  protected boolean isOutputName(String name) {
    String lower = name.toLowerCase();
    return lower.endsWith("out") || lower.endsWith("output");
  }

  /** Heuristik: typische Input-Namen enden auf "In" oder "input". */
  protected boolean isInputName(String name) {
    String lower = name.toLowerCase();
    return lower.endsWith("in") || lower.endsWith("input");
  }
}
