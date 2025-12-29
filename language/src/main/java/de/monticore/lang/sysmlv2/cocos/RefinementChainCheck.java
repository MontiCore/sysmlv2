package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlv2._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysml4verification._symboltable.PartDefSymbol;
import de.se_rwth.commons.SourcePositionBuilder;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RefinementChainCheck implements SysMLPartsASTPartDefCoCo {

  @Override
  public void check(ASTPartDef node) {
    if (!node.isPresentSymbol()) {
      return;
    }

    var partDefNameEnd = new SourcePositionBuilder().setFileName(node.get_SourcePositionStart().getFileName().get())
        .setLine(node.get_SourcePositionStart().getLine())
        .setColumn(node.get_SourcePositionStart().getColumn() + node.getName().length() + "part def".length() + 2)
        .build();

    if (node.getSymbol().getRequirementType() == ASTSysMLReqType.LLR || node.getSymbol().getRequirementType() == ASTSysMLReqType.MIXED) {

      if(getAllWithoutConnectionUsages(node.getSymbol().getTransitiveRefinements()).isEmpty()) {
        Log.warn("0x90022 A low level requirement should (at least indirectly) refine at least one basic requirement",
            node.get_SourcePositionStart(), partDefNameEnd);
      }

      if (node.getSysMLElementList().stream().anyMatch(e -> e instanceof ASTConnectionUsage)) {
        // Checks dedicated to basic LLR/Mixed-Compositions
        if (filterSimilarCompositions(node.getSymbol(), node.getSymbol().getTransitiveRefinements()).isEmpty()) {
          Log.warn("0x90011 A low level or mixed composition should refine a composition with similiar ConnectionUsages.",
              node.get_SourcePositionStart(),
              partDefNameEnd);
        }

      }
    }

    if (node.getSymbol().getRequirementType() == ASTSysMLReqType.HLR || node.getSymbol().getRequirementType() == ASTSysMLReqType.MIXED) {
      if(node.getSysMLElementList().stream().anyMatch(e -> e instanceof ASTConnectionUsage)) {
        // Checks dedicated to HLR/Mixed-Compositions
        if(getAllWithoutConnectionUsages(node.getSymbol().getTransitiveRefinements()).isEmpty()) {
          // High Level Architekturen / Kompositionen sollte auf ein "Basis Requirement" zurückzuführen sein,
          // welches das Verhalten nur anhand von beispielsweise Constraints beschreibt.
          Log.warn("0x90022 A high level composition should refine at least one basic requirement",
              node.get_SourcePositionStart(), partDefNameEnd);
        }

        if(filterSimilarCompositions(node.getSymbol(), node.getSymbol().getTransitiveRefiners()).isEmpty()) {
          Log.warn("0x90021 A high level composition should be refined by a composition with similiar ConnectionUsages.",
              node.get_SourcePositionStart(), partDefNameEnd);
        }

      } else {
        // Checks dedicated to Basic/Mixed-HLRs
        if(node.getSymbol().getTransitiveRefiners().isEmpty()) {
          Log.warn("0x90020 A basic high level or mixed requirement should be refined.",
              node.get_SourcePositionStart(), partDefNameEnd);

        }
      }
    }
  }

  /**
   * Attempts to filter the {@code symbols} for ones that are similarly composed as {@code reference}
   */
  private List<PartDefSymbol> filterSimilarCompositions(PartDefSymbol reference, List<PartDefSymbol> symbols) {
    var result = new ArrayList<PartDefSymbol>();
    var connectionUsages = reference.getAstNode().getSysMLElementList().stream()
        .filter(e -> e instanceof ASTConnectionUsage)
        .map(e -> (ASTConnectionUsage) e)
        .collect(Collectors.toList());

    for (var refinement : symbols) {
      var refConnectionUsages = refinement.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      if (connectionUsages.size() == refConnectionUsages.size()) {
        result.add(refinement);

      }
    }
    return result;
  }

  private List<PartDefSymbol> getAllWithoutConnectionUsages(List<PartDefSymbol> symbols) {
    return symbols.stream()
        .filter(e -> e.getAstNode().getSysMLElements(ASTConnectionUsage.class).isEmpty())
        .collect(Collectors.toList());
  }
}
