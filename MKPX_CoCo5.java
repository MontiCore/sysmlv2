/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * MKPX_CoCo5:
 * Outputs von Subkomponenten können nur zu Inputs von Subkomponenten oder Outputs der Oberkomponenten
 * verbunden werden.
 */
public class MKPX_CoCo5 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return; // keine Verbindung
    }

    ISysMLPartsScope scope = node.getEnclosingScope();

    EndpointResolution srcRes = resolveEndpoint(scope, node.getSrc());
    if (!srcRes.isResolved || !srcRes.isSubcomponent) {
      return; // Quelle ist kein a.b oder konnte nicht aufgelöst werden
    }

    if (srcRes.port == null || srcRes.port.getOutputAttributes().isEmpty()) {
      return; // Quelle ist kein Output-Port → Regel nicht anwendbar
    }

    // erlaubt sind a.sub (Input-Port) oder oberkomponentenPort a (Output-Port)
    EndpointResolution tgtRes = resolveEndpoint(scope, node.getTgt());
    if (!tgtRes.isResolved || tgtRes.port == null) {
      // gemeldet von MKPX_CoCo4
      return;
    }

    boolean validTgt;
    if (tgtRes.isSubcomponent) {
      // muss inputs besitzen
      validTgt = !tgtRes.port.getInputAttributes().isEmpty();
    } else {
      // muss outputs besitzen
      validTgt = !tgtRes.port.getOutputAttributes().isEmpty();
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

  /**
   * Versucht einen Endpunkt aufzulösen und liefert 
   * Art des Endpunkts zurück (Subkomponente vs. Oberkomponente)
   */
  protected EndpointResolution resolveEndpoint(ISysMLPartsScope scope, ASTEndpoint endpoint) {
    String qname = endpointQName(endpoint);
    EndpointResolution res = new EndpointResolution();

    if (qname.isEmpty()) {
      return res; // unresolved
    }

    String[] parts = qname.split("\\.");
    if (parts.length >= 2) {
      // a.b (qualifiziert) -> Subkomponente
      res.isSubcomponent = true;
      String partName = parts[0];
      String portName = parts[parts.length - 1];

      Optional<PartUsageSymbol> partOpt = scope.resolvePartUsageLocally(partName);
      if (partOpt.isEmpty()) {
        return res;
      }
      var partDefOpt = partOpt.get().getPartDef();
      if (partDefOpt.isEmpty()) {
        return res;
      }
      PartDefSymbol partDef = partDefOpt.get();
      var ports = partDef.getSpannedScope()
          .resolvePortUsageLocallyMany(false, portName, AccessModifier.ALL_INCLUSION, p -> true);
      if (ports.size() == 1) {
        res.port = ports.get(0);
        res.isResolved = true;
      }
      return res;
    }
    else {
      String portName = parts[0];
      var ports = scope.resolvePortUsageLocallyMany(false, portName, AccessModifier.ALL_INCLUSION, p -> true);
      if (ports.size() == 1) {
        res.port = ports.get(0);
        res.isResolved = true;
        res.isSubcomponent = false;
      }
      return res;
    }
  }

  /** Liefert den qualifizierten Namen des Endpunkts als String. */
  protected String endpointQName(ASTEndpoint ep) {
    return ep.getMCQualifiedName() != null ? ep.getMCQualifiedName().toString() : "";
  }

  protected static class EndpointResolution {
    boolean isResolved = false;
    boolean isSubcomponent = false;
    PortUsageSymbol port = null;
  }
}
