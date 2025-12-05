/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * MKPX_CoCo4:
 * In einer Verbindung "connect a.b to c.d" muss jeder verwendete (qualifizierte) Portname existieren.
 */
public class MKPX_CoCo4 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return; // keine Verbindung
    }

    ISysMLPartsScope scope = node.getEnclosingScope();

    checkEndpoint(scope, node.getSrc(), node);
    checkEndpoint(scope, node.getTgt(), node);
  }

  protected void checkEndpoint(ISysMLPartsScope scope,
                               ASTEndpoint endpoint,
                               ASTConnectionUsage conn) {
    String qname = endpointQName(endpoint);
    if (qname.isEmpty()) {
      return;
    }

    String[] parts = qname.split("\\.");

    // Qualifizierter Name: a.b -> resolve a (PartUsage), dann b in PartDef
    if (parts.length >= 2) {
      String partName = parts[0];
      String portName = parts[parts.length - 1];

      Optional<PartUsageSymbol> partOpt = scope.resolvePartUsageLocally(partName);

      if (partOpt.isEmpty()) {
        // Existenz der Subkomponente wird bereits in MKPX_CoCo3 geprüft.
        return;
      }

      var partDefOpt = partOpt.get().getPartDef();
      if (partDefOpt.isEmpty()) {
        Log.error(
            "0xMKPX04 The subcomponent '" + partName + "' does not reference a valid part definition.",
            conn.get_SourcePositionStart(),
            conn.get_SourcePositionEnd()
        );
        return;
      }

      PartDefSymbol partDef = partDefOpt.get();
      boolean portExistsInDef = partDef.getSpannedScope()
          .resolvePortUsageLocallyMany(false, portName, AccessModifier.ALL_INCLUSION, p -> true)
          .size() == 1;

      if (!portExistsInDef) {
        Log.error(
            "0xMKPX04 The port '" + portName + "' does not exist in the definition of subcomponent '"
                + partName + "'.",
            conn.get_SourcePositionStart(),
            conn.get_SourcePositionEnd()
        );
      }
    }
    else {
      // Unqualifiziert: Port der Oberkomponente muss lokal existieren
      String portName = parts[0];
      List<?> localPorts = scope.resolvePortUsageLocallyMany(
          false, portName, AccessModifier.ALL_INCLUSION, p -> true);
      if (localPorts.isEmpty()) {
        Log.error(
            "0xMKPX04 The port used in 'connect' '" + portName
                + "' does not exist in the parent component.",
            conn.get_SourcePositionStart(),
            conn.get_SourcePositionEnd()
        );
      }
    }
  }

  /** Liefert den qualifizierten Namen des Endpunkts als String. */
  protected String endpointQName(ASTEndpoint ep) {
    return ep.getMCQualifiedName() != null ? ep.getMCQualifiedName().toString() : "";
  }
}
