package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Checks that the ends of connections are ports, exist, and have the same type
 */
public class ConnectedPortsFitCoCo implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    // Skip validation if endpoints are missing
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return;
    }
    ISysMLPartsScope mainScope = node.getEnclosingScope();
    ASTEndpoint src = node.getSrc();
    ASTEndpoint tgt = node.getTgt();

    String srcQName = src.getMCQualifiedName().toString();
    String tgtQName = tgt.getMCQualifiedName().toString();

    boolean tgtIsSub = tgtQName.contains(".");
    boolean srcIsSub = srcQName.contains(".");

    if(!tgtIsSub && mainScope.resolvePortUsage(tgtQName).isEmpty()){
      Log.error("0x10AC0 Illegal connection: The port with the name " +
        tgtQName + " does not exist in the Parent Part: " + mainScope.getName(),
        node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );return;
    }
    if(!srcIsSub && mainScope.resolvePortUsage(srcQName).isEmpty()) {
      Log.error("0x10AC1 Illegal connection: The port with the name " +
        srcQName + " does not exist in the Parent Part: "+ mainScope.getName(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );return;
    }

    // If target is a subcomponent port, check if the port exists
    PortUsageSymbol tgtPort;
    PortUsageSymbol srcPort;
    if(tgtIsSub ) {
      tgtPort = resolveSubPortPart(mainScope, tgtQName);
    }
    else {
      tgtPort = resolveParPortPart(mainScope, tgtQName);
    }
    if(srcIsSub ) {
      srcPort = resolveSubPortPart(mainScope, srcQName);
    }
    else {
      srcPort = resolveParPortPart(mainScope, srcQName);
    }

    if(tgtPort == null){
      Log.error("0x10AC2 Illegal connection: The port with the name " +
              tgtQName + " does not exist in the Sub Part: " + tgtQName,
          node.get_SourcePositionStart(),node.get_SourcePositionEnd()
      );
      return;
    }
    else if (srcPort == null) {
      Log.error("0x10AC2 Illegal connection: The port with the name " +
              srcQName + " does not exist in the Sub Part: " + srcQName,
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
      return;
    }
    else{ //Vorbereitung für Typecheck
        String tgtPortType = getPortType(tgtPort);
        String srcPortType = getPortType(srcPort);
        if(!Objects.equals(tgtPortType, srcPortType)){
          Log.error("0x10AC4 Illegal connection: The type of the Ports " +
                  srcQName + " and  " + tgtQName + " are not the same. ",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd()
          );
        }
    }
  }

  protected String getPortType(PortUsageSymbol port) {
    List<SymTypeExpression> types = new ArrayList<>(port.getTypesList());
    types.addAll(port.getConjugatedTypesList());
    if (types.size() != 1) {
      Log.error("0x10AC3 Illegal connection: The port with the name " +
          port.getName() + " does not have exactly 1 Type "
      );
      return null;
    }
    return types.get(0).printFullName();
  }

  protected PortUsageSymbol resolveParPortPart(
      ISysMLPartsScope scope,
      String name)
  {
    Optional<PortUsageSymbol> portUsageSymbol = scope.resolvePortUsage(name);
    return portUsageSymbol.orElse(null);
  }

  protected PortUsageSymbol resolveSubPortPart(
      ISysMLPartsScope scope,
      String name)
  {
    int lastDot = name.lastIndexOf('.');
    if (lastDot < 0) {
      return null;
    }
    String partName = name.substring(0, lastDot);
    String portName = name.substring(lastDot + 1);
    Optional<PartUsageSymbol> partSymbol = scope.resolvePartUsage(partName);
    if (partSymbol.isEmpty()) {
      return null;
    }
    PartUsageSymbol pus = partSymbol.get();
    if(pus.getPartDef().isEmpty()){
      return null;
    }
    PartDefSymbol partDef = pus.getPartDef().get();
    ISysMLPartsScope partScope = partDef.getSpannedScope();
    Optional<PortUsageSymbol> ps = partScope.resolvePortUsage(portName);
    if (ps.isEmpty()) {
      return null;
    }
    PortUsageSymbol partPortSymbol = ps.get();
    return partPortSymbol;
  }
}
