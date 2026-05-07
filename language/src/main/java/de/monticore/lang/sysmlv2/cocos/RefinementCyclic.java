package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class RefinementCyclic implements SysMLPartsASTPartDefCoCo {

  /**
   * Zykel in Verfeinerungen sind unerwünscht.
   */
  @Override
  public void check(ASTPartDef node) {
    if (node == null || !node.isPresentSymbol()) {
      return;
    }
    var refiners = node.getSymbol().getTransitiveRefiners();
    var refinements = node.getSymbol().getTransitiveRefinements();

    var cyclicRefinements = refiners.stream().filter(refinements::contains)
        .map(PartDefSymbol::getName)
        .collect(Collectors.toList());
    var pos = node.get_SourcePositionStart();

    if (cyclicRefinements.size() > 0) {
      Log.error(
          "0x90030 Cyclic refinement detected: "
              + node.getName()
              + " <-> "
              + String.join(", ", cyclicRefinements),
          pos
      );
    }

    if (node.getRefinements().contains(node.getSymbol())) {
      Log.warn("0x90031 Trivial self-refinement", pos);
    }
  }
}
