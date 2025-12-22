/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTModifier;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * MKPX_CoCo5 = KBE_CoCo1
 * Checks that outputs of subcomponents can only be connected to:
 * 1. Inputs of subcomponents, or
 * 2. Outputs of the parent component.
 */
public class MKPX_CoCo5 implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // Skip validation if endpoints are missing
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();

    String srcQName = endpointQName(src);
    String tgtQName = endpointQName(tgt);

    ISysMLPartsScope scope = node.getEnclosingScope();

    // Determine if endpoints reference subcomponents
    boolean tgtIsSub = isSubcomponentEndpoint(tgtQName);
    boolean srcIsSub = isSubcomponentEndpoint(srcQName);

    // Resolve port symbols
    PortUsageSymbol tgtPort = resolvePortSymbol(scope, tgtQName, tgtIsSub);
    PortUsageSymbol srcPort = resolvePortSymbol(scope, srcQName, srcIsSub);

    // If any port is unresolvable, let other CoCos handle it
    if (srcPort == null || tgtPort == null) {
      return;
    }

    // Classify ports
    boolean tgtIsInput = portIsInput(tgtPort);
    boolean tgtIsOutput = portIsOutput(tgtPort);

    // Allowed connections:
    // 1. Subcomponent with Input ports
    // 2. Parent component with Output ports
    boolean allowed = (tgtIsSub && tgtIsInput) || (!tgtIsSub && tgtIsOutput);

    if(!srcIsSub || !portIsOutput(srcPort)){
      // Source is neither output nor Subcomponent
      // CoCo does not apply
      return;
    }
    if(
        (portIsOutput(srcPort) && portIsInput(srcPort)) ||
            (portIsOutput(tgtPort) && portIsInput(tgtPort))
    ) {
      Log.warn("0xMKPX05 Warning: Connection involves an 'inout' port which may have ambiguous directionality.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }

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

  /** Resolve port symbol based on whether it's in a subcomponent */
  protected PortUsageSymbol resolvePortSymbol(ISysMLPartsScope scope,
                                              String qname, boolean isSub) {
    if (isSub) {
      return resolvePortOfSubPart(scope, qname);
    } else {
      return resolvePort(scope, qname);
    }
  }

  /** Resolve PortUsageSymbol by qualified name */
  protected PortUsageSymbol resolvePort(ISysMLPartsScope scope, String qname) {
    List<PortUsageSymbol> result = scope.resolvePortUsageSubKinds(
        true,               // search in enclosing scopes
        qname,                          // qualified name, e.g., "a.out"
        AccessModifier.ALL_INCLUSION,   // ignore visibility
        p -> true       // no additional filtering
    );

    if (result.isEmpty()) {
      return scope.resolvePortUsage(qname).orElse(null);
    }
    // If multiple candidates, take the first (should be unique in your models)
    return result.get(0);
  }

  /** Resolve port within a subcomponent */
  protected PortUsageSymbol resolvePortOfSubPart(ISysMLPartsScope scope,
                                                 String qname) {
    // Split "a.out" into part "a" and port "out"
    int lastDot = qname.lastIndexOf('.');
    if (lastDot == -1) {
      return null;
    }

    String partName = qname.substring(0, lastDot);
    String portName = qname.substring(lastDot + 1);

    // Resolve the subcomponent
    Optional<PartUsageSymbol> partSymbol = scope.resolvePartUsage(partName);
    if (partSymbol.isEmpty()) {
      return null;
    }

    Optional<PartDefSymbol> partDef = partSymbol.get().getPartDef();
    if (partDef.isPresent()) {
      ISysMLPartsScope partScope = partDef.get().getSpannedScope();
      return resolvePort(partScope, portName);
    }

    return null;
  }

  /** Extract modifiers from PortUsageSymbol */
  protected ASTModifier getModifiersFromPortUsageSymbol(PortUsageSymbol symbol) {
    ASTAttributeUsage portAttributeUsageAST = (ASTAttributeUsage)
        symbol.getAstNode()
            .getPortDefs()
            .get(0)
            .getSysMLElementList()
            .get(0);
    return portAttributeUsageAST.getModifier();
  }

  protected boolean portIsInput(PortUsageSymbol symbol) {
    ASTModifier mods = getModifiersFromPortUsageSymbol(symbol);
    return mods.isIn();
  }

  protected boolean portIsOutput(PortUsageSymbol symbol) {
    ASTModifier mods = getModifiersFromPortUsageSymbol(symbol);
    return mods.isOut();
  }
}
