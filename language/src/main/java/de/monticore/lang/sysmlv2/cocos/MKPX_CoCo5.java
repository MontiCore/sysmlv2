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

/**
 * MKPX_CoCo5 = KBE_CoCo1
 */
public class MKPX_CoCo5 implements SysMLPartsASTConnectionUsageCoCo {

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

    boolean tgtIsSub = isSubcomponentEndpoint(tgtQName);
    boolean tgtHasInputPins = !tgtPort.getInputAttributes().isEmpty();
    boolean tgtHasOutputPins = !tgtPort.getOutputAttributes().isEmpty();

    // Erlaubt:
    //   a) Sub-Port mit Output-Ports -> Sub-Port mit Input-Ports
    //   b) Sub-Port mit Output-Ports -> Parent-Port mit Output-Ports
    // Zurzeit werden Output-Pins geprüft und nihct Ports
    boolean allowed =
        (tgtIsSub && tgtHasInputPins) ||           // Subkomponente mit Inputs
            (!tgtIsSub && tgtHasOutputPins);           // Oberkomponente mit Outputs

    if (!allowed) {
      Log.error(
          "0xMKPX05 Illegal connection: outputs of subcomponents can only be " +
              "connected to inputs of subcomponents or outputs of the parent component.",
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
   * z.B. "a.out". Ohne Punkt = Port der Oberkomponente.
   */
  protected boolean isSubcomponentEndpoint(String qname) {
    return qname.contains(".");
  }

  /** PortUsageSymbol über den qualified Name auflösen. */
  protected PortUsageSymbol resolvePort(ISysMLPartsScope scope, String qname) {
    List<PortUsageSymbol> result =
        scope.resolvePortUsageSubKinds(
            true,                           // auch in umgebenden Scopes suchen
            qname,                          // qualified name, z.B. "a.out"
            AccessModifier.ALL_INCLUSION,   // Sichtbarkeiten egal
            p -> true                       // kein zusätzliches Filter
        );
    if (result.isEmpty()) {
      return null;
    }
    // falls mehrere Kandidaten, nimm den ersten – in deinen Modellen sollte es eindeutig sein
    return result.get(0);;
  }
}
