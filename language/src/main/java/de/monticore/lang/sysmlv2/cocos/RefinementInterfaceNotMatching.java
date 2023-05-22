package de.monticore.lang.sysmlv2.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class RefinementInterfaceNotMatching implements SysMLPartsASTPartDefCoCo {

  /**
   * Damit eine Verfeinerung funktionieren kann, müssen die Schnittstellen übereinstimmen,
   * da sich sonst keine Teilmengen-Relation zwischen zwei Mengen von Funktionen (SPS) formulieren lässt.
   */
  @Override
  public void check(ASTPartDef node) {
    if (node == null || node.getEnclosingScope() == null) {
      return;
    }

    var notMatching = node.getRefinements().stream()
        .filter(refinement -> !node.getSymbol().matchesInterfaceOf(refinement))
        .collect(Collectors.toList());

    if (notMatching.size() > 0){
      var pos = node.getSpecializationList().stream()
          .filter(s -> s instanceof ASTSysMLRefinement)
          .map(ASTNode::get_SourcePositionStart)
          .findFirst()
          .orElse(node.get_SourcePositionStart());

      for (var refinement : notMatching){
        Log.error("0x9004 Interface of refinement " + refinement.getName() + " is incompatible.", pos);
      }
    }
  }
}
