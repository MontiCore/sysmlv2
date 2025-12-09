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
 * MKPX_CoCo6:
 * Inputs von Oberkomponenten können nur zu Inputs von Subkomponenten oder Outputs der
 * Oberkomponenten verbunden werden.
 */
public class MKPX_CoCo6 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return; // keine Verbindung
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
    boolean srcHasInputPins = !srcPort.getInputAttributes().isEmpty();

    // --- Ziel klassifizieren ---
    boolean tgtIsSub = isSubcomponentEndpoint(tgtQName);
    boolean tgtHasOutputPins = !tgtPort.getOutputAttributes().isEmpty();

    // Verboten: Oberkomponenten-Input -> Subkomponenten-Output
    boolean forbidden = (!srcIsSub && srcHasInputPins) &&  // Quelle: Input der Oberkomponente
        (tgtIsSub && tgtHasOutputPins);    // Ziel: Output einer Subkomponente

    if (forbidden) {
      Log.error(
          "0xMKPX06 Illegal connection: inputs of the parent component must not be " +
              "connected to outputs of subcomponents.",
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
            true,
            qname,
            AccessModifier.ALL_INCLUSION,
            p -> true
        );
    if (result.isEmpty()) {
      return null;
    }
    return result.get(0);
  }
}
