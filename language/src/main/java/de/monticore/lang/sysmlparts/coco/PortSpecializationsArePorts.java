package de.monticore.lang.sysmlparts.coco;

import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Stream;

/**
 * Checks specializations of ports to be ports. Works on both port defs (checking all specializations to be port
 * defs) and on port usages (specializations can be either "subsetted" port usages or port defs). The CoCo doesn't
 * check for "correct" keyword use, as the official specification is somewhat vague in that regard (i.e., the author
 * of this CoCo does not fully comprehend the terms "subset", "specialize", "redefine").
 */
public class PortSpecializationsArePorts
    implements SysMLPartsASTPortUsageCoCo, SysMLPartsASTPortDefCoCo
{

  /** All superTypes must be port def again, i.e., adapted using PortDef2TypeSymbol! */
  @Override
  public void check(ASTPortDef node) {
    // TODO Grammar symbolrule superTypes:SymTypeExpression hinzufügen + hier CoCo implementieren wie PortUsage unten
  }

  @Override
  public void check(ASTPortUsage node) {
    Stream.concat(node.getSymbol().getTypesList().stream(), node.getSymbol().getConjugatedTypesList().stream())
        .filter(t -> !(t.getTypeInfo() instanceof PortDef2TypeSymbolAdapter))
        .forEach(t -> Log.error(
            "0xMPf001 " + t.print() + ": Specializations of port usages must be port usages or definitions.",
            node.get_SourcePositionStart(), node.get_SourcePositionEnd()));
  }

}
