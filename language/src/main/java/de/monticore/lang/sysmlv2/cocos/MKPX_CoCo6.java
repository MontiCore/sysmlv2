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

    ISysMLPartsScope scope = node.getEnclosingScope();

    // Target ist ein Input-Port der Oberkomponente
    EndpointResolution tgtRes = resolveEndpoint(scope, node.getTgt());
    if (!tgtRes.isResolved || tgtRes.isSubcomponent) {
      return; 
    }

    if (tgtRes.port == null || tgtRes.port.getInputAttributes().isEmpty()) {
      return; // Target ist kein Input-Port der Oberkomponente
    }

    EndpointResolution srcRes = resolveEndpoint(scope, node.getSrc());
    if (!srcRes.isResolved || srcRes.port == null) {
      // in MKPX_CoCo4 gemeldet
      return;
    }

    boolean validTgt;
    if (srcRes.isSubcomponent) {
      // muss inputs besitzen
      validTgt = !srcRes.port.getInputAttributes().isEmpty();
    } else {
      // muss outputs besitzen
      validTgt = !srcRes.port.getOutputAttributes().isEmpty();
    }

    if (!validTgt) {
      Log.error(
          "0xMKPX06 Invalid connection direction: An input of the parent component can only be " +
              "connected to inputs of subcomponents or outputs of the parent component.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Versucht einen Endpunkt aufzulösen und liefert
   * Typ zurück (Subkomponente oder Oberkomponente)
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
      // Oberkomponenten-Port
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
