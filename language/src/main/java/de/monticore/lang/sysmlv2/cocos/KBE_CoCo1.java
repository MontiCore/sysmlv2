/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class KBE_CoCo1 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {

    // Wenn eine Seite fehlt, kann man nichts Sinnvolles prüfen
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

    // Wenn ein Port nicht auflösbar ist, kümmern sich andere CoCos darum
    if (srcPort == null || tgtPort == null) {
      return;
    }

    // --- Quelle klassifizieren ---

    boolean srcIsSub = isSubcomponentEndpoint(srcQName);
    boolean srcHasOutputPins = !srcPort.getOutputAttributes().isEmpty();

    // Regel gilt nur für: "Outputs von Subkomponenten"
    if (!(srcIsSub && srcHasOutputPins)) {
      return;
    }

    // --- Ziel klassifizieren ---

    boolean tgtIsSub    = isSubcomponentEndpoint(tgtQName);
    boolean tgtIsParent = !tgtIsSub;

    boolean tgtHasInputPins  = !tgtPort.getInputAttributes().isEmpty();
    boolean tgtHasOutputPins = !tgtPort.getOutputAttributes().isEmpty();

    // Erlaubt:
    //   a) Sub-Port mit Output-Pins -> Sub-Port mit Input-Pins
    //   b) Sub-Port mit Output-Pins -> Parent-Port mit Output-Pins
    boolean allowed =
        (tgtIsSub    && tgtHasInputPins) ||
        (tgtIsParent && tgtHasOutputPins);

    if (!allowed) {
      Log.error(
          "0xKBE01 Illegal connection in ConnectionUsage '" + node.getName()
              + "': ports of subcomponents that have output pins may only be "
              + "connected to ports of subcomponents that have input pins or "
              + "to ports of the parent component that have output pins.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  // -------- Hilfsmethoden --------

  /** Qualified Name des Endpunkts als String. */
  protected String endpointQName(ASTEndpoint ep) {
    if (ep.getMCQualifiedName() != null) {
      return ep.getMCQualifiedName().toString();
    }
    return "";
  }

  /**
   * Heuristik: ein Name mit '.' steht für einen Port einer Subkomponente,
   * z.B. "tele.maniacOut". Ohne Punkt = Port der Oberkomponente.
   */
  protected boolean isSubcomponentEndpoint(String qname) {
    return qname.contains(".");
  }

  /** PortUsageSymbol über den qualified Name auflösen. */
  protected PortUsageSymbol resolvePort(ISysMLPartsScope scope, String qname) {
    List<PortUsageSymbol> result =
        scope.resolvePortUsageSubKinds(
            true,                           // auch in umgebenden Scopes suchen
            qname,                          // qualified name, z.B. "tele.maniacOut"
            AccessModifier.ALL_INCLUSION,   // Sichtbarkeiten egal
            p -> true                       // kein zusätzliches Filter
        );
    if (result.isEmpty()) {
      return null;
    }
    // falls mehrere Kandidaten, nimm den ersten – in deinen Modellen sollte es eindeutig sein
    return result.get(0);
  }
}
