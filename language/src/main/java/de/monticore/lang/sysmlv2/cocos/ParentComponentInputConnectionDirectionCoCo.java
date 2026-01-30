/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTModifier;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * ParentComponentInputConnectionDirectionCoCo
 * Inputs von Oberkomponenten können nur zu Inputs von Subkomponenten oder Outputs der Oberkomponenten verbunden werden
 * (alternative Formulierung):
 * Inputs von Oberkomponenten können nicht zu Outputs von Subkomponenten verbunden werden.
 */
public class ParentComponentInputConnectionDirectionCoCo implements SysMLPartsASTConnectionUsageCoCo {

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
    boolean srcIsInput = portIsInput(srcPort);
    boolean srcIsOutput = portIsOutput(srcPort);

    // Allowed connections:
    // 1. Subcomponent with Input ports
    // 2. Parent component with Output ports
    // 3. Connection does not imply flow directions.
    //    This information must be inferred by port attributes
    boolean allowed = true;

    if (!((srcIsInput && !srcIsSub) || (tgtIsInput && !tgtIsSub))) {
      // Both Endpoints are not a Parent input
      // CoCo does not apply
      return;
    } else {
      // At least one endpoint is of the Parent component
      // Check if the OTHER endpoint satisfies the rule

      // If src is parent input
      if (srcIsInput && !srcIsSub) {
        allowed = allowed && (
            (tgtIsInput && tgtIsSub) ||           // tgt is sub input
                (tgtIsOutput && !tgtIsSub)        // tgt is parent output
        );
      }

      // If tgt is parent input
      if (tgtIsInput && !tgtIsSub) {
        allowed = allowed && (
            (srcIsInput && srcIsSub) ||           // src is sub input
                (srcIsOutput && !srcIsSub)        // src is parent output
        );
      }
    }

    if(
        (portIsInOutput(srcPort)) ||
        (portIsInOutput(tgtPort))
    ) {
      Log.warn("0x10AA6 Warning: Connection involves an 'inout' port which may have ambiguous directionality.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }

    if (!allowed) {
      Log.error(
          "0x10AA6 Illegal connection: inputs of parent components can only be " +
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
    boolean portIsInAndNotConjugated = mods.isIn() && !portIsConjugated(symbol);
    boolean portIsOutAndConjugated = mods.isOut() && portIsConjugated(symbol);
    return (portIsInAndNotConjugated || portIsOutAndConjugated);
  }

  protected boolean portIsOutput(PortUsageSymbol symbol) {
    ASTModifier mods = getModifiersFromPortUsageSymbol(symbol);
    boolean portIsOutAndNotConjugated = mods.isOut() && !portIsConjugated(symbol);
    boolean portIsInAndConjugated = mods.isIn() && portIsConjugated(symbol);
    return (portIsOutAndNotConjugated || portIsInAndConjugated);
  }

  protected boolean portIsInOutput(PortUsageSymbol symbol) {
    ASTModifier mods = getModifiersFromPortUsageSymbol(symbol);
    return mods.isInout();
  }

  protected boolean portIsConjugated(PortUsageSymbol symbol) {
    return
        ((ASTSysMLTyping) symbol
            .getAstNode()
            .getSpecialization(0))
            .isConjugated();
  }
}
