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
 * MKPX_CoCo5:Outputs von Subkomponenten können nur zu 
 * Inputs von Subkomponenten oder Outputs der Oberkomponenten verbunden werden.
 */
public class MKPX_CoCo5 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    if (node == null || !node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    ISysMLPartsScope scope = node.getEnclosingScope();
    if (scope == null) {
      return;
    }

    EndpointResolution srcRes = resolveEndpoint(scope, node.getSrc());
    if (!srcRes.isResolved || !srcRes.isSubcomponent || srcRes.port == null) {
      return;
    }

    // Quelle muss Output-Port einer Subkomponente sein
    if (srcRes.port.getOutputAttributes() == null || 
        srcRes.port.getOutputAttributes().isEmpty()) {
      return; // Kein Output-Port
    }

    EndpointResolution tgtRes = resolveEndpoint(scope, node.getTgt());
    if (!tgtRes.isResolved || tgtRes.port == null) {
      return; // Wird von CoCo4 gemeldet
    }

    boolean validTgt;
    if (tgtRes.isSubcomponent) {
      // Ziel muss Input-Port einer Subkomponente sein
      validTgt = tgtRes.port.getInputAttributes() != null && 
                 !tgtRes.port.getInputAttributes().isEmpty();
    } else {
      // Ziel muss Output-Port der Oberkomponente sein  
      validTgt = tgtRes.port.getOutputAttributes() != null && 
                 !tgtRes.port.getOutputAttributes().isEmpty();
    }

    if (!validTgt) {
      Log.error(
          "0xMKPX05 Invalid connection direction: An output of a subcomponent can only be " +
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
    return result.get(0);
  }
}
