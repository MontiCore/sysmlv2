package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlbasis._ast.ASTModifier;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class ParentSubConnectionCoCo implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // Skip validation if endpoints are missing
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }

    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();

    String srcQName = src.getMCQualifiedName().toString();
    String tgtQName = tgt.getMCQualifiedName().toString();

    ISysMLPartsScope scope = node.getEnclosingScope();

    // Determine if endpoints reference subcomponents
    boolean tgtIsSub = isSubcomp(tgtQName);
    boolean srcIsSub = isSubcomp(srcQName);

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

    boolean parOutToSubIn = (srcIsOutput && !srcIsSub && tgtIsInput && tgtIsSub)
        ||(srcIsInput && srcIsSub && tgtIsOutput && !tgtIsSub);
    boolean parOutToParOut = (srcIsOutput && !srcIsSub && tgtIsOutput && !tgtIsSub);
    boolean subInTosubIn = (srcIsInput && srcIsSub && tgtIsInput && tgtIsSub);
    boolean parInToParIn = !srcIsSub && !tgtIsSub &&  srcIsInput && tgtIsInput;
    boolean subOutToSubOut = srcIsSub && tgtIsSub && srcIsOutput && tgtIsOutput;
    boolean subOutToParIn = (srcIsSub && srcIsOutput && !tgtIsSub && tgtIsInput)
        || (tgtIsSub && tgtIsOutput && !srcIsSub && srcIsInput);
    if (parOutToParOut) {
      Log.error(
          "0x10AB0 Illegal connection: A parent output port was " +
              "connected to a parent output port. ",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );
    }
    if (parOutToSubIn) {
      Log.error(
          "0x10AB1 Illegal connection: A parent output port was " +
              "connected to an input port of its subcomponent. ",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );
    }
    if (subInTosubIn) {
      Log.error("0x10AB2 Illegal connection: A subcomponent input port was " +
              "connected to a subcomponent input port. ",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );
    }
    if (parInToParIn) {
      Log.error("0x10AB3 Illegal connection: A parent input port was " +
              "connected to a parent input port. ",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
    if (subOutToParIn) {
      Log.error("0x10AB4 Illegal connection: A subcomponent output port was " +
              "connected to a parent input port. ",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );
    }
    if (subOutToSubOut) {
      Log.error("0x10AB5 Illegal connection: A subcomponent output port was " +
              "connected to a subcomponent output port. ",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );
    }
    if((portIsInOutput(srcPort)) ||(portIsInOutput(tgtPort))
    ) {
      Log.warn("0x10AA6 Warning: Connection involves an 'inout' port which may have ambiguous directionality.",
          node.get_SourcePositionStart(),node.get_SourcePositionEnd());
    }

  }

  // -------- Hilfsmethoden --------

  /**
   * Heuristik: ein Name mit '.' steht für einen Port einer Subkomponente,
   * z.B. "a.out". Ohne Punkt = Port der Oberkomponente.
   */
  protected boolean isSubcomp(String qname) {
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


  protected boolean portIsInput(PortUsageSymbol symbol) {
    return !symbol.getInputAttributes().isEmpty();
  }
  protected boolean portIsOutput(PortUsageSymbol symbol) {
    return !symbol.getOutputAttributes().isEmpty();
  }
  protected boolean portIsInOutput(PortUsageSymbol symbol) {
    return portIsInput(symbol) && portIsOutput(symbol);
  }

}
